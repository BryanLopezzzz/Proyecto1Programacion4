import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { adminEmpresasPendientes, adminAprobarEmpresa, adminRechazarEmpresa } from '../../api/api'

export default function AdminEmpresas() {
  const [pendientes, setPendientes] = useState([])
  const nav = useNavigate()

  const cargar = () => adminEmpresasPendientes().then(setPendientes).catch(() => {})
  useEffect(() => { cargar() }, [])

  return (
    <div className="container">
      <div className="top-bar"><h2>Empresas Pendientes de Aprobación</h2></div>
      {pendientes.length === 0
        ? <p className="empty">No hay empresas pendientes.</p>
        : <table>
            <thead><tr><th>Nombre</th><th>Correo</th><th>Teléfono</th><th>Localización</th><th>Descripción</th><th>Acciones</th></tr></thead>
            <tbody>
              {pendientes.map(e => (
                <tr key={e.usuarioId}>
                  <td>{e.nombre}</td><td>{e.correo}</td><td>{e.telefono}</td><td>{e.localizacion}</td>
                  <td>{e.descripcion.substring(0, 60)}…</td>
                  <td>
                    <button className="btn btn-primary btn-sm" onClick={async () => { await adminAprobarEmpresa(e.usuarioId); cargar() }}> Aprobar</button>
                    <button className="btn btn-secondary btn-sm" onClick={async () => { await adminRechazarEmpresa(e.usuarioId); cargar() }}> Rechazar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
      }
      <br /><button className="btn btn-outline" onClick={() => nav('/admin/dashboard')}>← Dashboard</button>
    </div>
  )
}
