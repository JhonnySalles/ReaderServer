import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Tabs, TabItem } from './Tabs'

describe('Tabs Component', () => {
  const mockTabs: TabItem[] = [
    { id: 'all', label: 'Todos', count: 12 },
    { id: 'favorites', label: 'Favoritos', count: 3 },
    { id: 'recent', label: 'Recentes' }
  ]

  it('deve renderizar todos os tabs com suas labels e contagens', () => {
    render(
      <Tabs
        tabs={mockTabs}
        activeTab="all"
        onChange={() => {}}
      />
    )

    expect(screen.getByText('Todos')).toBeInTheDocument()
    expect(screen.getByText('Favoritos')).toBeInTheDocument()
    expect(screen.getByText('Recentes')).toBeInTheDocument()
    expect(screen.getByText('12')).toBeInTheDocument()
    expect(screen.getByText('3')).toBeInTheDocument()
  })

  it('deve aplicar a classe tab-active no item ativo', () => {
    render(
      <Tabs
        tabs={mockTabs}
        activeTab="favorites"
        onChange={() => {}}
      />
    )

    const favoritesButton = screen.getByRole('button', { name: /favoritos/i })
    expect(favoritesButton).toHaveClass('tab-active')

    const allButton = screen.getByRole('button', { name: /todos/i })
    expect(allButton).not.toHaveClass('tab-active')
  })

  it('deve chamar a função onChange com o ID correto ao clicar em uma aba', async () => {
    const handleTabChange = vi.fn()
    const user = userEvent.setup()

    render(
      <Tabs
        tabs={mockTabs}
        activeTab="all"
        onChange={handleTabChange}
      />
    )

    const recentButton = screen.getByRole('button', { name: /recentes/i })
    await user.click(recentButton)

    expect(handleTabChange).toHaveBeenCalledTimes(1)
    expect(handleTabChange).toHaveBeenCalledWith('recent')
  })
})
