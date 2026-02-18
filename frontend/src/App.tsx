import { FormEvent, useEffect, useMemo, useState } from 'react'
import './App.css'

type Product = { id: number; code: string; name: string; price: number }
type Material = { id: number; code: string; name: string; stockQty: number }
type BomItem = { id: { productId: number; materialId: number }; product: Product; material: Material; qtyNeeded: number }
type PlanItem = { productId: number; productCode: string; productName: string; unitPrice: number; plannedQty: number; totalValue: number }
type PlanResponse = { items: PlanItem[]; totalValue: number }

type ProductForm = { id?: number; code: string; name: string; price: string }
type MaterialForm = { id?: number; code: string; name: string; stockQty: string }
type BomForm = { productId: string; materialId: string; qtyNeeded: string }

const API_BASE = (import.meta as any).env.VITE_API_BASE || 'http://localhost:8080'

const emptyProduct: ProductForm = { code: '', name: '', price: '' }
const emptyMaterial: MaterialForm = { code: '', name: '', stockQty: '' }
const emptyBom: BomForm = { productId: '', materialId: '', qtyNeeded: '' }

async function api<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(init?.headers || {}) },
    ...init
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || `HTTP ${res.status}`)
  }
  if (res.status === 204) return undefined as T
  return (await res.json()) as T
}

export default function App() {
  const [products, setProducts] = useState<Product[]>([])
  const [materials, setMaterials] = useState<Material[]>([])
  const [bomByProduct, setBomByProduct] = useState<Record<number, BomItem[]>>({})
  const [plan, setPlan] = useState<PlanResponse>({ items: [], totalValue: 0 })
  const [selectedProductId, setSelectedProductId] = useState<number | null>(null)

  const [productForm, setProductForm] = useState<ProductForm>(emptyProduct)
  const [materialForm, setMaterialForm] = useState<MaterialForm>(emptyMaterial)
  const [bomForm, setBomForm] = useState<BomForm>(emptyBom)

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  async function loadAll() {
    setLoading(true)
    setError('')
    try {
      const [p, m, planning] = await Promise.all([
        api<Product[]>('/products'),
        api<Material[]>('/materials'),
        api<PlanResponse>('/planning/suggestion')
      ])
      setProducts(p)
      setMaterials(m)
      setPlan(planning)

      const firstId = p.length ? p[0].id : null
      const current = selectedProductId && p.some((x) => x.id === selectedProductId) ? selectedProductId : firstId
      setSelectedProductId(current)
      if (current != null) {
        setBomForm((prev) => ({ ...prev, productId: String(current) }))
        await loadBom(current)
      } else {
        setBomByProduct({})
      }
    } catch (e) {
      setError((e as Error).message)
    } finally {
      setLoading(false)
    }
  }

  async function loadBom(productId: number) {
    const list = await api<BomItem[]>(`/bom/product/${productId}`)
    setBomByProduct((prev) => ({ ...prev, [productId]: list }))
  }

  useEffect(() => {
    void loadAll()
  }, [])

  const selectedProductBom = useMemo(() => {
    if (selectedProductId == null) return []
    return bomByProduct[selectedProductId] || []
  }, [bomByProduct, selectedProductId])

  async function submitProduct(e: FormEvent) {
    e.preventDefault()
    try {
      const body = { code: productForm.code, name: productForm.name, price: Number(productForm.price) }
      if (productForm.id) {
        await api(`/products/${productForm.id}`, { method: 'PUT', body: JSON.stringify(body) })
      } else {
        await api('/products', { method: 'POST', body: JSON.stringify(body) })
      }
      setProductForm(emptyProduct)
      await loadAll()
    } catch (err) {
      setError((err as Error).message)
    }
  }

  async function deleteProduct(id: number) {
    try {
      await api(`/products/${id}`, { method: 'DELETE' })
      if (selectedProductId === id) setSelectedProductId(null)
      await loadAll()
    } catch (err) {
      setError((err as Error).message)
    }
  }

  async function submitMaterial(e: FormEvent) {
    e.preventDefault()
    try {
      const body = { code: materialForm.code, name: materialForm.name, stockQty: Number(materialForm.stockQty) }
      if (materialForm.id) {
        await api(`/materials/${materialForm.id}`, { method: 'PUT', body: JSON.stringify(body) })
      } else {
        await api('/materials', { method: 'POST', body: JSON.stringify(body) })
      }
      setMaterialForm(emptyMaterial)
      await loadAll()
    } catch (err) {
      setError((err as Error).message)
    }
  }

  async function deleteMaterial(id: number) {
    try {
      await api(`/materials/${id}`, { method: 'DELETE' })
      await loadAll()
    } catch (err) {
      setError((err as Error).message)
    }
  }

  async function submitBom(e: FormEvent) {
    e.preventDefault()
    try {
      if (!bomForm.productId) throw new Error('Select a product before adding BOM association.')
      const body = {
        productId: Number(bomForm.productId),
        materialId: Number(bomForm.materialId),
        qtyNeeded: Number(bomForm.qtyNeeded)
      }
      await api('/bom', { method: 'POST', body: JSON.stringify(body) })
      setBomForm((prev) => ({ ...emptyBom, productId: prev.productId }))
      if (selectedProductId != null) await loadBom(selectedProductId)
      setPlan(await api<PlanResponse>('/planning/suggestion'))
    } catch (err) {
      setError((err as Error).message)
    }
  }

  async function deleteBom(productId: number, materialId: number) {
    try {
      await api(`/bom/product/${productId}/material/${materialId}`, { method: 'DELETE' })
      await loadBom(productId)
      setPlan(await api<PlanResponse>('/planning/suggestion'))
    } catch (err) {
      setError((err as Error).message)
    }
  }

  return (
    <main className="app">
      <header className="hero">
        <h1>Autoflex Inventory</h1>
        <p>Product, raw material, BOM and production planning management.</p>
        <div className="meta">
          <span>API: {API_BASE}</span>
          <button onClick={() => void loadAll()} disabled={loading}>{loading ? 'Refreshing...' : 'Refresh'}</button>
        </div>
      </header>

      {error && <div className="alert">{error}</div>}

      <section className="grid">
        <article className="card">
          <h2>Products</h2>
          <form onSubmit={submitProduct} className="form">
            <input placeholder="Code" value={productForm.code} onChange={(e) => setProductForm({ ...productForm, code: e.target.value })} required />
            <input placeholder="Name" value={productForm.name} onChange={(e) => setProductForm({ ...productForm, name: e.target.value })} required />
            <input placeholder="Price" type="number" step="0.01" min="0.01" value={productForm.price} onChange={(e) => setProductForm({ ...productForm, price: e.target.value })} required />
            <div className="formActions">
              <button type="submit">{productForm.id ? 'Update' : 'Create'}</button>
              <button type="button" className="ghost" onClick={() => setProductForm(emptyProduct)}>Clear</button>
            </div>
          </form>
          <table>
            <thead>
              <tr><th>Code</th><th>Name</th><th>Price</th><th /></tr>
            </thead>
            <tbody>
              {products.map((p) => (
                <tr key={p.id} className={selectedProductId === p.id ? 'selected' : ''}>
                  <td>{p.code}</td>
                  <td>{p.name}</td>
                  <td>{p.price.toFixed(2)}</td>
                  <td className="actions">
                    <button onClick={() => { setSelectedProductId(p.id); setProductForm({ id: p.id, code: p.code, name: p.name, price: String(p.price) }) }}>Edit</button>
                    <button className="danger" onClick={() => void deleteProduct(p.id)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </article>

        <article className="card">
          <h2>Raw Materials</h2>
          <form onSubmit={submitMaterial} className="form">
            <input placeholder="Code" value={materialForm.code} onChange={(e) => setMaterialForm({ ...materialForm, code: e.target.value })} required />
            <input placeholder="Name" value={materialForm.name} onChange={(e) => setMaterialForm({ ...materialForm, name: e.target.value })} required />
            <input placeholder="Stock Qty" type="number" step="0.001" min="0.001" value={materialForm.stockQty} onChange={(e) => setMaterialForm({ ...materialForm, stockQty: e.target.value })} required />
            <div className="formActions">
              <button type="submit">{materialForm.id ? 'Update' : 'Create'}</button>
              <button type="button" className="ghost" onClick={() => setMaterialForm(emptyMaterial)}>Clear</button>
            </div>
          </form>
          <table>
            <thead>
              <tr><th>Code</th><th>Name</th><th>Stock</th><th /></tr>
            </thead>
            <tbody>
              {materials.map((m) => (
                <tr key={m.id}>
                  <td>{m.code}</td>
                  <td>{m.name}</td>
                  <td>{m.stockQty.toFixed(3)}</td>
                  <td className="actions">
                    <button onClick={() => setMaterialForm({ id: m.id, code: m.code, name: m.name, stockQty: String(m.stockQty) })}>Edit</button>
                    <button className="danger" onClick={() => void deleteMaterial(m.id)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </article>
      </section>

      <section className="grid">
        <article className="card">
          <h2>Bill Of Materials (BOM)</h2>
          <p>Select a product to manage required raw materials.</p>
          <select
            value={selectedProductId ?? ''}
            onChange={async (e) => {
              const id = Number(e.target.value)
              setSelectedProductId(id)
              setBomForm((prev) => ({ ...prev, productId: String(id) }))
              await loadBom(id)
            }}
          >
            <option value="" disabled>Select Product</option>
            {products.map((p) => <option key={p.id} value={p.id}>{p.code} - {p.name}</option>)}
          </select>

          <form onSubmit={submitBom} className="form compact">
            <input value={bomForm.productId} placeholder="Product Id" disabled />
            <input value={bomForm.materialId} onChange={(e) => setBomForm({ ...bomForm, materialId: e.target.value })} placeholder="Material Id" required />
            <input value={bomForm.qtyNeeded} onChange={(e) => setBomForm({ ...bomForm, qtyNeeded: e.target.value })} type="number" step="0.001" min="0.001" placeholder="Qty Needed" required />
            <button type="submit">Save Association</button>
          </form>

          <table>
            <thead>
              <tr><th>Product</th><th>Material</th><th>Qty Needed</th><th /></tr>
            </thead>
            <tbody>
              {selectedProductBom.map((b) => (
                <tr key={`${b.product.id}-${b.material.id}`}>
                  <td>{b.product.code}</td>
                  <td>{b.material.code}</td>
                  <td>{Number(b.qtyNeeded).toFixed(3)}</td>
                  <td className="actions">
                    <button className="danger" onClick={() => void deleteBom(b.product.id, b.material.id)}>Delete</button>
                  </td>
                </tr>
              ))}
              {!selectedProductBom.length && (
                <tr><td colSpan={4}>No BOM items for selected product.</td></tr>
              )}
            </tbody>
          </table>
        </article>

        <article className="card">
          <h2>Production Suggestion</h2>
          <p>Prioritized by highest unit value and constrained by available stock.</p>
          <table>
            <thead>
              <tr><th>Code</th><th>Name</th><th>Unit Price</th><th>Planned Qty</th><th>Total Value</th></tr>
            </thead>
            <tbody>
              {plan.items.map((item) => (
                <tr key={item.productId}>
                  <td>{item.productCode}</td>
                  <td>{item.productName}</td>
                  <td>{Number(item.unitPrice).toFixed(2)}</td>
                  <td>{Number(item.plannedQty).toFixed(0)}</td>
                  <td>{Number(item.totalValue).toFixed(2)}</td>
                </tr>
              ))}
              {!plan.items.length && <tr><td colSpan={5}>No producible items with current stock.</td></tr>}
            </tbody>
          </table>
          <div className="total">Total Suggested Value: {Number(plan.totalValue || 0).toFixed(2)}</div>
        </article>
      </section>
    </main>
  )
}
