import { useEffect, useState } from 'react'
import { getCaracteristicas, getMonedas, buscarPuestosPublicos } from '../../api/api'
import ArbolCheckboxes from '../../components/ArbolCheckboxes'

const nivelTexto = n => n === 1 ? 'Básico' : n === 2 ? 'Intermedio' : 'Avanzado'

export default function Buscar() {
  const [raices, setRaices] = useState([])
  const [monedas, setMonedas] = useState([])
  const [seleccionadas, setSeleccionadas] = useState([])
  const [modoTodos, setModoTodos] = useState(false)
  const [monedaId, setMonedaId] = useState('')
  const [resultados, setResultados] = useState([])
  const [buscado, setBuscado] = useState(false)

  useEffect(() => {
    getCaracteristicas().then(setRaices).catch(() => {})
    getMonedas().then(setMonedas).catch(() => {})
  }, [])

  const buscar = async () => {
    const params = { modoTodos }
    if (seleccionadas.length) params.caracteristicas = seleccionadas
    if (monedaId) params.monedaId = monedaId
    const res = await buscarPuestosPublicos(params)
    setResultados(res || [])
    setBuscado(true)
  }

  const limpiar = () => { setSeleccionadas([]); setMonedaId(''); setResultados([]); setBuscado(false) }

  return (
    <div className="container-wide">
      <h2>Buscar puestos por características</h2>
      <ArbolCheckboxes raices={raices} seleccionadas={seleccionadas} onChange={setSeleccionadas} />
      <div style={{ marginBottom: 16 }}>
        <label>Filtrar por moneda (opcional)</label>
        <select value={monedaId} onChange={e => setMonedaId(e.target.value)} style={{ maxWidth: 280 }}>
          <option value="">-- Todas las monedas --</option>
          {monedas.map(m => <option key={m.id} value={m.id}>{m.nombre} ({m.codigo})</option>)}
        </select>
      </div>
      <div className="modo">
        <strong>Modo:</strong>
        <label><input type="radio" checked={!modoTodos} onChange={() => setModoTodos(false)} /> Cualquiera (OR)</label>
        <label><input type="radio" checked={modoTodos} onChange={() => setModoTodos(true)} /> Todos (AND)</label>
      </div>
      <div className="btns">
        <button className="btn btn-primary" onClick={buscar}>Buscar</button>
        <button className="btn btn-outline" onClick={limpiar}>Limpiar</button>
      </div>
      <h3>Resultados</h3>
      {buscado && resultados.length === 0 && <p className="empty">No se encontraron resultados.</p>}
      {resultados.length > 0 && (
        <table>
          <thead><tr><th>Empresa</th><th>Descripción</th><th>Salario</th><th>Moneda</th><th>Características</th></tr></thead>
          <tbody>
            {resultados.map(p => (
              <tr key={p.id}>
                <td>{p.empresa.nombre}</td>
                <td>{p.descripcion}</td>
                <td>{Number(p.salario).toLocaleString('es-CR')}</td>
                <td>{p.moneda.codigo}</td>
                <td>{(p.caracteristicas || []).map(c => `${c.nombre} (${nivelTexto(c.nivel)})`).join(', ')}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
