import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getCaracteristicas, getMonedas, empresaPublicarPuesto } from '../../api/api'

export default function NuevoPuesto() {
  const [raices, setRaices] = useState([])
  const [monedas, setMonedas] = useState([])
  const [descripcion, setDescripcion] = useState('')
  const [salario, setSalario] = useState('')
  const [monedaId, setMonedaId] = useState('')
  const [tipo, setTipo] = useState('')
  const [error, setError] = useState('')
  const arbolRef = useRef(null)
  const nav = useNavigate()

  useEffect(() => {
    getCaracteristicas().then(setRaices).catch(() => {})
    getMonedas().then(setMonedas).catch(() => {})
  }, [])

  const renderArbol = (nodos, depth = 0) => nodos.map(n => (
    <li key={n.id} style={{ marginLeft: depth * 16, margin: `6px 0 6px ${depth * 16}px` }}>
      <label>
        <input type="checkbox" data-id={n.id} />
        <span style={{ marginLeft: 7 }}>{n.nombre}</span>
      </label>
      <select name={`nivel_${n.id}`} className="nivel-sel">
        <option value="1">Básico</option>
        <option value="2">Intermedio</option>
        <option value="3">Avanzado</option>
      </select>
      {n.hijos && n.hijos.length > 0 && <ul style={{ listStyle: 'none', padding: 0 }}>{renderArbol(n.hijos, depth + 1)}</ul>}
    </li>
  ))

  const submit = async () => {
    setError('')
    if (!descripcion || !salario || !monedaId || !tipo) return setError('Todos los campos son requeridos')
    const caracteristicas = []
    arbolRef.current.querySelectorAll('input[type=checkbox]:checked').forEach(cb => {
      const id = parseInt(cb.getAttribute('data-id'))
      const nivelSel = arbolRef.current.querySelector(`select[name="nivel_${id}"]`)
      caracteristicas.push({ id, nivel: nivelSel ? parseInt(nivelSel.value) : 2 })
    })
    try {
      await empresaPublicarPuesto({ descripcion, salario: parseFloat(salario), monedaId: parseInt(monedaId), tipo, caracteristicas })
      nav('/empresa/puestos')
    } catch (e) { setError(e.message) }
  }

  return (
    <div className="container-narrow" style={{ padding: '40px 10%', maxWidth: 720 }}>
      <h2>Publicar Nuevo Puesto</h2>
      {error && <div className="alert-error">{error}</div>}
      <label>Descripción del puesto *</label>
      <textarea value={descripcion} onChange={e => setDescripcion(e.target.value)} placeholder="Descripción detallada…" />
      <div className="row2">
        <div>
          <label>Salario *</label>
          <input type="number" value={salario} onChange={e => setSalario(e.target.value)} placeholder="800000" />
        </div>
        <div>
          <label>Moneda *</label>
          <select value={monedaId} onChange={e => setMonedaId(e.target.value)}>
            <option value="">-- Seleccione --</option>
            {monedas.map(m => <option key={m.id} value={m.id}>{m.nombre} ({m.codigo})</option>)}
          </select>
        </div>
      </div>
      <label>Tipo de publicación *</label>
      <select value={tipo} onChange={e => setTipo(e.target.value)}>
        <option value="">-- Seleccione --</option>
        <option value="PUBLICO">Público (visible para todos)</option>
        <option value="PRIVADO">Privado (solo oferentes registrados)</option>
      </select>
      <label>Características requeridas</label>
      <div className="arbol" ref={arbolRef}>
        <ul style={{ listStyle: 'none', padding: 0 }}>{renderArbol(raices)}</ul>
      </div>
      <button className="btn btn-primary" onClick={submit}>Publicar Puesto</button>
    </div>
  )
}
