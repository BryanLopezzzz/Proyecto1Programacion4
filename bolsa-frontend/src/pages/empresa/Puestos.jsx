import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { empresaAPI } from '../../api/api'
import { Toast } from '../../components/UI'

export default function EmpresaPuestos() {
  const [puestos, setPuestos] = useState([])
  const [toast, setToast] = useState(null)
  const navigate = useNavigate()

  useEffect(() => { empresaAPI.puestos().then(data => setPuestos(data || [])) }, [])

  const desactivar = async (id) => {
    if (!confirm('¿Desactivar este puesto?')) return
    try {
      await empresaAPI.desactivar(id)
      setToast({ msg: 'Puesto desactivado', ok: true })
      empresaAPI.puestos().then(data => setPuestos(data || []))
    } catch (e) { setToast({ msg: e.message, ok: false }) }
  }

  return (
    <div className="container">
      {toast && <Toast msg={toast.msg} ok={toast.ok} onClose={() => setToast(null)} />}
      <div className="top-bar">
        <h2>Mis Puestos</h2>
        <button className="btn btn-primary" onClick={() => navigate('/empresa/puestos/nuevo')}>+ Publicar nuevo puesto</button>
      </div>
      {puestos.length === 0
        ? <p className="empty">No has publicado puestos aún.</p>
        : (
          <table>
            <thead><tr><th>ID</th><th>Descripción</th><th>Salario</th><th>Moneda</th><th>Tipo</th><th>Activo</th><th>Acciones</th></tr></thead>
            <tbody>
              {puestos.map(p => (
                <tr key={p.id}>
                  <td>{p.id}</td>
                  <td>{p.descripcion.substring(0, 55)}{p.descripcion.length > 55 ? '…' : ''}</td>
                  <td>{Number(p.salario).toLocaleString('es-CR')}</td>
                  <td>{p.moneda.codigo}</td>
                  <td>{p.tipo}</td>
                  <td><span className={`badge ${p.activo ? 'badge-activo' : 'badge-inactivo'}`}>{p.activo ? 'Sí' : 'No'}</span></td>
                  <td>
                    {p.activo && <button className="btn btn-secondary btn-sm" onClick={() => desactivar(p.id)}>Desactivar</button>}
                    <button className="btn btn-primary btn-sm" onClick={() => navigate(`/empresa/puestos/${p.id}/candidatos`)}>Buscar candidatos</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )
      }
    </div>
  )
}
