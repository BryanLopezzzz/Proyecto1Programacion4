import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { adminCaracArbol, adminCaracTodos, adminCrearCarac } from '../../api/api'

function RenderArbol({ nodos, depth = 0 }) {
  return nodos.map(n => (
      <div key={n.id} style={{ marginLeft: depth * 20, marginBottom: 6 }}>
      <span style={{
        color: depth === 0 ? 'var(--accent)' : 'var(--text-secondary)',
        fontWeight: depth === 0 ? 700 : 400,
        fontSize: depth === 0 ? 14 : 13
      }}>
        {depth > 0 ? '↳ ' : ''}{n.nombre}
      </span>
        {/* Solo renderiza hijos si estamos en nivel 0 */}
        {depth === 0 && n.hijos && n.hijos.length > 0 && (
            <RenderArbol nodos={n.hijos} depth={depth + 1} />
        )}
      </div>
  ))
}

export default function AdminCaracteristicas() {
  const [arbol, setArbol] = useState([])
  const [todos, setTodos] = useState([])
  const [nombre, setNombre] = useState('')
  const [padreId, setPadreId] = useState('')
  const nav = useNavigate()

  const cargar = () => Promise.all([adminCaracArbol(), adminCaracTodos()])
    .then(([a, t]) => { setArbol(a); setTodos(t) }).catch(() => {})

  useEffect(() => { cargar() }, [])

  const [error, setError] = useState('')
  const crear = async () => {
    if (!nombre.trim()) return
    setError('')
    try {
      await adminCrearCarac({ nombre: nombre.trim(), padreId: padreId || null })
      setNombre(''); setPadreId(''); cargar()
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <div className="container panels" style={{ display: 'flex', gap: 28, flexWrap: 'wrap', padding: '40px 10%' }}>
      <div className="panel-left" style={{ flex: 1, minWidth: 320 }}>
        <h2>Árbol de Características</h2>
        <div className="arbol" style={{ maxHeight: 'none' }}><RenderArbol nodos={arbol} /></div>
      </div>
      <div style={{ minWidth: 260 }}>
        <div className="card">
          <h3>Nueva Característica</h3>
          <label>Nombre *</label>
          <input type="text" value={nombre} onChange={e => setNombre(e.target.value)} placeholder="Ej: Java" />
          <label>Nodo padre (opcional)</label>
          <select value={padreId} onChange={e => setPadreId(e.target.value)}>
            <option value="">-- Raíz (sin padre) --</option>
            {todos
                .filter(n => !n.tienePadre)
                .map(n => (
                    <option key={n.id} value={n.id}>{n.nombre}</option>
                ))
            }
          </select>
          {error && <div className="alert-error" style={{ marginTop: 12 }}>{error}</div>}
          <button className="btn btn-primary" style={{ marginTop: 16, width: '100%' }} onClick={crear}>+ Agregar</button>
        </div>
        <button className="btn btn-outline" style={{ width: '100%', marginTop: 8 }} onClick={() => nav('/admin/dashboard')}> Dashboard</button>
      </div>
    </div>
  )
}
