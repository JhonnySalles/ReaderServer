package br.com.fenix.readerserver.service

import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import java.net.InetAddress
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo

@Configuration
@ConditionalOnProperty(name = ["descobertaLocal"], havingValue = "true")
class LocalDiscoveryService(
    @Value("\${server.port}") private val port: Int,
    @Value("\${spring.application.name}") private val appName: String
) : ApplicationListener<ApplicationReadyEvent> {

    private val oLog: Logger = LoggerFactory.getLogger(LocalDiscoveryService::class.java)
    private var jmdns: JmDNS? = null

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        try {
            oLog.info("--> Condição de descoberta local ATIVADA. Registrando serviço mDNS...")
            jmdns = JmDNS.create(InetAddress.getLocalHost())
            val serviceInfo = ServiceInfo.create("_http._tcp.local.", appName, port, "ReaderServer API")

            jmdns?.registerService(serviceInfo)
            oLog.info("--> Serviço '$appName' anunciado na rede local na porta $port.")
        } catch (e: Exception) {
            oLog.error("!!! Falha ao registrar o serviço de descoberta mDNS.", e)
        }
    }

    @PreDestroy
    fun unregisterService() {
        if (jmdns != null) {
            oLog.info("<-- Encerrando a aplicação. Removendo anúncio do serviço mDNS...")
            jmdns?.unregisterAllServices()
            jmdns?.close()
            oLog.info("<-- Anúncio do serviço removido da rede.")
        }
    }
}
