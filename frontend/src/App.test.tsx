import { render, screen, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import App from './App'

function jsonResponse(body: unknown) {
  return {
    ok: true,
    status: 200,
    json: async () => body,
    text: async () => JSON.stringify(body)
  } as Response
}

describe('App', () => {
  beforeEach(() => {
    vi.stubGlobal(
      'fetch',
      vi.fn(async (input: RequestInfo | URL) => {
        const url = String(input)
        if (url.includes('/products')) {
          return jsonResponse([{ id: 1, code: 'P-001', name: 'Product A', price: 10.5 }])
        }
        if (url.includes('/materials')) {
          return jsonResponse([{ id: 1, code: 'RM-001', name: 'Steel', stockQty: 100 }])
        }
        if (url.includes('/planning/suggestion')) {
          return jsonResponse({ items: [], totalValue: 0 })
        }
        if (url.includes('/bom/product/1')) {
          return jsonResponse([])
        }
        return jsonResponse([])
      })
    )
  })

  afterEach(() => {
    vi.unstubAllGlobals()
    vi.restoreAllMocks()
  })

  it('renders title and loads initial data', async () => {
    render(<App />)

    expect(screen.getByRole('heading', { name: 'Autoflex Inventory' })).toBeTruthy()

    await waitFor(() => {
      expect(screen.getByText('P-001')).toBeTruthy()
      expect(screen.getByText('RM-001')).toBeTruthy()
    })
  })
})
