import { useEffect, useState } from 'react'
import { getCaracteristicas, getMonedas, buscarPuestosPublicos } from '../../api/api'

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

    const toggleCaracteristica = (id) => {
        setSeleccionadas((actuales) =>
            actuales.includes(id)
                ? actuales.filter((x) => x !== id)
                : [...actuales, id]
        )
    }

    const buscar = async () => {
        const params = { modoTodos }

        if (seleccionadas.length) {
            params.caracteristicas = seleccionadas
        }

        if (monedaId) {
            params.monedaId = monedaId
        }

        const res = await buscarPuestosPublicos(params)
        setResultados(res || [])
        setBuscado(true)
    }

    const limpiar = () => {
        setSeleccionadas([])
        setMonedaId('')
        setResultados([])
        setBuscado(false)
    }

    return (
        <div className="container-wide buscar-puestos-page">
            <section className="buscar-hero">
                <div>
                    <span className="buscar-etiqueta">Búsqueda pública</span>
                    <h2>Buscar puestos por características</h2>
                    <p>
                        Explora los puestos públicos disponibles usando filtros por habilidades,
                        características y tipo de moneda.
                    </p>
                </div>

                <div className="buscar-resumen">
                    <strong>{seleccionadas.length}</strong>
                    <span>características seleccionadas</span>
                </div>
            </section>

            <section className="filtros-premium">
                <div className="filtros-premium-header">
                    <div>
                        <h3>Filtros de búsqueda</h3>
                        <p>
                            Selecciona las características que deseas buscar y, si lo necesitas,
                            filtra también por moneda.
                        </p>
                    </div>
                </div>

                <div className="filtro-moneda-box">
                    <label>Filtrar por moneda</label>

                    <select
                        value={monedaId}
                        onChange={(e) => setMonedaId(e.target.value)}
                        className="moneda-select"
                    >
                        <option value="">Todas las monedas</option>
                        {monedas.map((m) => (
                            <option key={m.id} value={m.id}>
                                {m.nombre} ({m.codigo})
                            </option>
                        ))}
                    </select>
                </div>

                <div className="skills-grid">
                    {raices.map((categoria) => (
                        <div key={categoria.id} className="skill-category-card">
                            <div className="skill-category-title">
                                <span className="skill-icon">{'\u2726'}</span>
                                <strong>{categoria.nombre}</strong>
                            </div>

                            <div className="skill-options">
                                {categoria.hijos && categoria.hijos.length > 0 ? (
                                    categoria.hijos.map((hijo) => (
                                        <label
                                            key={hijo.id}
                                            className={`skill-option ${
                                                seleccionadas.includes(hijo.id) ? 'selected' : ''
                                            }`}
                                        >
                                            <input
                                                type="checkbox"
                                                checked={seleccionadas.includes(hijo.id)}
                                                onChange={() => toggleCaracteristica(hijo.id)}
                                            />
                                            <span>{hijo.nombre}</span>
                                        </label>
                                    ))
                                ) : (
                                    <label
                                        className={`skill-option ${
                                            seleccionadas.includes(categoria.id) ? 'selected' : ''
                                        }`}
                                    >
                                        <input
                                            type="checkbox"
                                            checked={seleccionadas.includes(categoria.id)}
                                            onChange={() => toggleCaracteristica(categoria.id)}
                                        />
                                        <span>{categoria.nombre}</span>
                                    </label>
                                )}
                            </div>
                        </div>
                    ))}
                </div>

                <div className="modo-premium">
                    <div>
                        <strong>Modo de búsqueda</strong>
                        <span>Define si el puesto debe coincidir con una o con todas las características.</span>
                    </div>

                    <div className="modo-opciones">
                        <label className={!modoTodos ? 'active' : ''}>
                            <input
                                type="radio"
                                checked={!modoTodos}
                                onChange={() => setModoTodos(false)}
                            />
                            Cualquiera
                        </label>

                        <label className={modoTodos ? 'active' : ''}>
                            <input
                                type="radio"
                                checked={modoTodos}
                                onChange={() => setModoTodos(true)}
                            />
                            Todos
                        </label>
                    </div>
                </div>

                <div className="acciones-busqueda">
                    <button className="btn btn-primary" onClick={buscar}>
                        Buscar puestos
                    </button>

                    <button className="btn btn-outline" onClick={limpiar}>
                        Limpiar
                    </button>
                </div>
            </section>

            <section className="resultados-section">
                <div className="resultados-header">
                    <h3>Resultados</h3>

                    {buscado && (
                        <span>
              {resultados.length} puesto{resultados.length === 1 ? '' : 's'} encontrado
                            {resultados.length === 1 ? '' : 's'}
            </span>
                    )}
                </div>

                {buscado && resultados.length === 0 && (
                    <p className="empty">No se encontraron resultados.</p>
                )}

                <div className="public-result-grid">
                    {resultados.map((p) => (
                        <article key={p.id} className="public-result-card">
                            <div className="public-result-header">
                                <div>
                                    <h3>{p.empresa.nombre}</h3>
                                    <span className="puesto-result-tipo publico">
                    {p.tipo}
                  </span>
                                </div>

                                <div className="public-salary">
                                    <strong>{p.moneda.codigo}</strong>
                                    <span>{Number(p.salario).toLocaleString('es-CR')}</span>
                                </div>
                            </div>

                            <p className="public-description">
                                {p.descripcion}
                            </p>

                            <div className="public-caracteristicas">
                                {(p.caracteristicas || []).map((c) => (
                                    <span key={c.id} className="coincidencia-tag">
                    {c.nombre} · {nivelTexto(c.nivel)}
                  </span>
                                ))}
                            </div>
                        </article>
                    ))}
                </div>
            </section>
        </div>
    )
}