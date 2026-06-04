import { useEffect, useState } from 'react'
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
    const [seleccionadas, setSeleccionadas] = useState({})

    const nav = useNavigate()

    useEffect(() => {
        getCaracteristicas().then(setRaices).catch(() => {})
        getMonedas().then(setMonedas).catch(() => {})
    }, [])

    const toggleCaracteristica = (id) => {
        setSeleccionadas((actuales) => {
            const copia = { ...actuales }

            if (copia[id]) {
                delete copia[id]
            } else {
                copia[id] = 2
            }

            return copia
        })
    }

    const cambiarNivel = (id, nivel) => {
        setSeleccionadas((actuales) => ({
            ...actuales,
            [id]: Number(nivel)
        }))
    }

    const limpiar = () => {
        setDescripcion('')
        setSalario('')
        setMonedaId('')
        setTipo('')
        setSeleccionadas({})
        setError('')
    }

    const submit = async () => {
        setError('')

        if (!descripcion || !salario || !monedaId || !tipo) {
            setError('Todos los campos marcados con * son requeridos')
            return
        }

        const caracteristicas = Object.entries(seleccionadas).map(([id, nivel]) => ({
            id: Number(id),
            nivel: Number(nivel)
        }))

        try {
            await empresaPublicarPuesto({
                descripcion,
                salario: parseFloat(salario),
                monedaId: parseInt(monedaId),
                tipo,
                caracteristicas
            })

            limpiar()
            nav('/empresa/puestos')
        } catch (e) {
            setError(e.message)
        }
    }


    const totalSeleccionadas = Object.keys(seleccionadas).length

    return (
        <div className="container-wide nuevo-puesto-page">
            <section className="nuevo-puesto-hero">
                <div>
                    <span className="buscar-etiqueta">Empresa</span>
                    <h2>Publicar Nuevo Puesto</h2>
                    <p>
                        Complete la información del puesto, defina el tipo de publicación y seleccione
                        las características requeridas para encontrar mejores candidatos.
                    </p>
                </div>

                <div className="buscar-resumen">
                    <strong>{totalSeleccionadas}</strong>
                    <span>requisitos seleccionados</span>
                </div>
            </section>

            {error && <div className="alert-error">{error}</div>}

            <section className="nuevo-puesto-panel">
                <div className="form-section-header">
                    <h3>Información general del puesto</h3>
                    <p>Datos principales que verán los oferentes al consultar la oferta laboral.</p>
                </div>

                <div className="form-card-grid">
                    <div className="form-card form-card-full">
                        <label>Descripción del puesto *</label>
                        <textarea
                            value={descripcion}
                            onChange={(e) => setDescripcion(e.target.value)}
                            placeholder="Ej: Se requiere docente de informática con experiencia en tecnologías web..."
                        />
                    </div>

                    <div className="form-card">
                        <label>Salario *</label>
                        <input
                            type="number"
                            value={salario}
                            onChange={(e) => setSalario(e.target.value)}
                            placeholder="Ej: 800000"
                        />
                    </div>

                    <div className="form-card">
                        <label>Moneda *</label>
                        <select value={monedaId} onChange={(e) => setMonedaId(e.target.value)}>
                            <option value="">Seleccione una moneda</option>
                            {monedas.map((m) => (
                                <option key={m.id} value={m.id}>
                                    {m.nombre} ({m.codigo})
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-card form-card-full">
                        <label>Tipo de publicación *</label>

                        <div className="tipo-publicacion-grid">
                            <label className={`tipo-publicacion-card ${tipo === 'PUBLICO' ? 'active' : ''}`}>
                                <input
                                    type="radio"
                                    name="tipo"
                                    value="PUBLICO"
                                    checked={tipo === 'PUBLICO'}
                                    onChange={(e) => setTipo(e.target.value)}
                                />
                                <span className="tipo-icon">{'\u{1F310}'}</span>
                                <div>
                                    <strong>Público</strong>
                                    <small>Visible para todos los usuarios.</small>
                                </div>
                            </label>

                            <label className={`tipo-publicacion-card ${tipo === 'PRIVADO' ? 'active' : ''}`}>
                                <input
                                    type="radio"
                                    name="tipo"
                                    value="PRIVADO"
                                    checked={tipo === 'PRIVADO'}
                                    onChange={(e) => setTipo(e.target.value)}
                                />
                                <span className="tipo-icon">{'\u{1F512}'}</span>
                                <div>
                                    <strong>Privado</strong>
                                    <small>Visible solo para oferentes registrados.</small>
                                </div>
                            </label>
                        </div>
                    </div>
                </div>
            </section>

            <section className="filtros-premium requisitos-panel">
                <div className="filtros-premium-header">
                    <div>
                        <h3>Características requeridas</h3>
                        <p>
                            Seleccione las habilidades necesarias para el puesto e indique el nivel requerido.
                        </p>
                    </div>
                </div>

                <div className="requisitos-grid">
                    {raices.map((categoria) => (
                        <div key={categoria.id} className="requisito-category-card">
                            <div className="skill-category-title">
                                <span className="skill-icon"></span>
                                <strong>{categoria.nombre}</strong>
                            </div>

                            <div className="requisito-options">
                                {categoria.hijos && categoria.hijos.length > 0 ? (
                                    categoria.hijos.map((hijo) => (
                                        <div
                                            key={hijo.id}
                                            className={`requisito-option ${
                                                seleccionadas[hijo.id] ? 'selected' : ''
                                            }`}
                                        >
                                            <label>
                                                <input
                                                    type="checkbox"
                                                    checked={!!seleccionadas[hijo.id]}
                                                    onChange={() => toggleCaracteristica(hijo.id)}
                                                />
                                                <span>{hijo.nombre}</span>
                                            </label>

                                            {seleccionadas[hijo.id] && (
                                                <select
                                                    className="nivel-requisito-select"
                                                    value={seleccionadas[hijo.id]}
                                                    onChange={(e) => cambiarNivel(hijo.id, e.target.value)}
                                                >
                                                    <option value={1}>Básico</option>
                                                    <option value={2}>Intermedio</option>
                                                    <option value={3}>Avanzado</option>
                                                </select>
                                            )}
                                        </div>
                                    ))
                                ) : (
                                    <div
                                        className={`requisito-option ${
                                            seleccionadas[categoria.id] ? 'selected' : ''
                                        }`}
                                    >
                                        <label>
                                            <input
                                                type="checkbox"
                                                checked={!!seleccionadas[categoria.id]}
                                                onChange={() => toggleCaracteristica(categoria.id)}
                                            />
                                            <span>{categoria.nombre}</span>
                                        </label>

                                        {seleccionadas[categoria.id] && (
                                            <select
                                                className="nivel-requisito-select"
                                                value={seleccionadas[categoria.id]}
                                                onChange={(e) => cambiarNivel(categoria.id, e.target.value)}
                                            >
                                                <option value={1}>Básico</option>
                                                <option value={2}>Intermedio</option>
                                                <option value={3}>Avanzado</option>
                                            </select>
                                        )}
                                    </div>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            </section>

            <div className="acciones-publicar">
                <button className="btn btn-primary" onClick={submit}>
                    Publicar puesto
                </button>

                <button className="btn btn-outline" onClick={limpiar}>
                    Limpiar formulario
                </button>

                <button className="btn btn-secondary" onClick={() => nav('/empresa/puestos')}>
                    Volver
                </button>
            </div>
        </div>
    )
}