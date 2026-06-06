import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getPuestoDetalle } from '../../api/api'

const nivelTexto = n => n === 1 ? 'Básico' : n === 2 ? 'Intermedio' : 'Avanzado'

export default function PuestoDetalle() {
    const { id } = useParams()
    const nav = useNavigate()
    const [puesto, setPuesto] = useState(null)

    useEffect(() => {
        getPuestoDetalle(id).then(setPuesto).catch(() => nav('/'))
    }, [id])

    if (!puesto) return <div className="loading">Cargando…</div>

    return (
        <div className="container-narrow" style={{ padding: '40px 10%', maxWidth: 720 }}>
            <button className="btn btn-outline btn-sm" onClick={() => nav('/')}>← Volver</button>
            <div className="card" style={{ marginTop: 20 }}>
                <p style={{ color: 'var(--text-tertiary)', fontSize: 12, marginBottom: 4 }}>
                    {puesto.empresa.nombre}
                </p>
                <h2>{puesto.descripcion}</h2>
                <div className="info-row" style={{ marginTop: 16 }}>
                    <span className="lbl">Salario:</span>
                    <span className="val">{puesto.moneda.codigo} {Number(puesto.salario).toLocaleString('es-CR')}</span>
                </div>
                <div className="info-row">
                    <span className="lbl">Tipo:</span>
                    <span className={`badge ${puesto.tipo === 'PUBLICO' ? 'badge-publico' : 'badge-privado'}`}>
            {puesto.tipo}
          </span>
                </div>
            </div>

            {puesto.caracteristicas?.length > 0 && (
                <div className="card">
                    <h3>Requisitos</h3>
                    <table>
                        <thead><tr><th>Característica</th><th>Nivel requerido</th></tr></thead>
                        <tbody>
                        {puesto.caracteristicas.map(c => (
                            <tr key={c.id}>
                                <td>{c.nombre}</td>
                                <td><span className="badge badge-activo">{nivelTexto(c.nivel)}</span></td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    )
}