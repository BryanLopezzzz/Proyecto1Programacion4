
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getPuestosPublicos } from '../../api/api'

export default function Inicio() {
    const [puestos, setPuestos] = useState([])
    const [hoverId, setHoverId] = useState(null)
    const nav = useNavigate()

    useEffect(() => { getPuestosPublicos().then(setPuestos).catch(() => {}) }, [])

    return (
        <div style={{ padding: '48px 5%' }}>
            <h1>Bolsa de Empleo</h1>
            <p className="subtitulo-seccion">Últimos 5 puestos públicos</p>

            {/* contenedor con scroll horizontal */}
            <div style={{
                display: 'flex',
                flexDirection: 'row',
                flexWrap: 'nowrap',
                alignItems: 'flex-start',
                gap: '16px',
                overflowX: 'auto',
                overflowY: 'visible',
                paddingBottom: '180px',
                paddingTop: '8px',
                width: '100%',
            }}>
                {puestos.map(p => (
                    <div
                        key={p.id}
                        onMouseEnter={() => setHoverId(p.id)}
                        onMouseLeave={() => setHoverId(null)}
                        style={{
                            background: 'var(--bg-surface)',
                            border: hoverId === p.id ? '1.5px solid var(--accent-medium)' : '1px solid var(--border)',
                            borderRadius: 'var(--radius-lg)',
                            width: '230px',
                            flexShrink: 0,
                            padding: '22px',
                            position: 'relative',
                            boxShadow: hoverId === p.id ? 'var(--shadow-hover)' : 'var(--shadow-sm)',
                            transition: 'box-shadow 0.2s, border-color 0.2s, transform 0.2s',
                            transform: hoverId === p.id ? 'translateY(-4px)' : 'none',
                            zIndex: hoverId === p.id ? 10 : 1,
                            cursor: 'default',
                        }}
                    >
                        <div style={{ fontWeight: 700, color: 'var(--accent)', fontSize: 'var(--font-size-md)', marginBottom: 6 }}>
                            {p.empresa.nombre}
                        </div>

                        <div style={{ fontSize: 'var(--font-size-sm)', color: 'var(--text-secondary)', marginBottom: 14, lineHeight: 1.5 }}>
                            {p.descripcion.substring(0, 50)}...
                        </div>

                        <span style={{ fontWeight: 700, color: 'var(--text-primary)', fontSize: 'var(--font-size-md)', display: 'block', marginBottom: 18 }}>
              {p.moneda.codigo} {Number(p.salario).toLocaleString('es-CR')}
            </span>

                        <a
                            className="btn btn-outline"
                            style={{ display: 'block', textAlign: 'center', width: '100%', cursor: 'pointer' }}
                            onClick={() => nav('/buscar')}
                        >
                            Ver detalle
                        </a>

                        {hoverId === p.id && (
                            <div style={{
                                position: 'absolute',
                                top: 'calc(100% + 8px)',
                                left: 0,
                                right: 0,
                                background: 'var(--bg-surface)',
                                border: '1px solid var(--border)',
                                padding: '16px',
                                borderRadius: 'var(--radius-md)',
                                zIndex: 200,
                                boxShadow: 'var(--shadow-lg)',
                                fontSize: 'var(--font-size-sm)',
                                color: 'var(--text-primary)',
                            }}>
                                <strong style={{ display: 'block', marginBottom: 8, fontWeight: 600, lineHeight: 1.4 }}>
                                    {p.descripcion}
                                </strong>
                                {(p.caracteristicas || []).length > 0 ? (
                                    <ul style={{ paddingLeft: 16, margin: 0 }}>
                                        {(p.caracteristicas || []).map(c => (
                                            <li key={c.id} style={{ margin: '4px 0', color: 'var(--text-secondary)', fontSize: 'var(--font-size-xs)' }}>
                                                {c.nombre} (nivel {c.nivel})
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <span style={{ color: 'var(--text-tertiary)', fontSize: 'var(--font-size-xs)', fontStyle: 'italic' }}>
                    Sin características requeridas
                  </span>
                                )}
                            </div>
                        )}
                    </div>
                ))}
            </div>

            {puestos.length === 0 && <p className="empty">No hay puestos publicados aún.</p>}
        </div>
    )
}