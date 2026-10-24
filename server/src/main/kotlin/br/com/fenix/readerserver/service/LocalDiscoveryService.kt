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
    @Value("\${spring.application.name}") private val appName: String,
    @Value("\${mdns.host-address:}") private val hostAddress: String,
    @Value("\${mdns.service-type:_readerserver._tcp.local.}") private val serviceType: String,
    @Value("\${api.version:1.0.0}") private val apiVersion: String
) : ApplicationListener<ApplicationReadyEvent> {

    private val oLog: Logger = LoggerFactory.getLogger(LocalDiscoveryService::class.java)
    private var jmdns: JmDNS? = null

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        try {
            oLog.info("--> Condição de descoberta local ATIVADA. Registrando serviço mDNS...")
            val bindAddress = if (hostAddress.isNotBlank()) {
                oLog.info("--> Usando endereço de anúncio mDNS configurado: $hostAddress")
                InetAddress.getByName(hostAddress.trim())
            } else {
                InetAddress.getLocalHost()
            }
            jmdns = JmDNS.create(bindAddress)

            val props = mapOf(
                "version" to apiVersion,
                "path" to "/api",
                "name" to appName
            )
            val serviceInfo = ServiceInfo.create(serviceType, appName, port, 0, 0, props)

            jmdns?.registerService(serviceInfo)
            oLog.info("--> Serviço '$appName' ($serviceType) anunciado na rede local em ${bindAddress.hostAddress}:$port.")
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
