import { useEffect, useState } from 'react'
import { getCaracteristicas, oferenteBuscarPuestos } from '../../api/api'
import BarraCoincidencia from '../../components/BarraCoincidencia'

const estadoTexto = {
    CUMPLE: '✓',
    PARCIAL: '~',
    INSUFICIENTE: '!',
    AUSENTE: '✗'
}

export default function BuscarPuestos() {
    const [raices, setRaices] = useState([])
    const [seleccionadas, setSeleccionadas] = useState([])
    const [modoTodos, setModoTodos] = useState(false)
    const [resultados, setResultados] = useState([])
    const [buscado, setBuscado] = useState(false)

    useEffect(() => {
        getCaracteristicas().then(setRaices).catch(() => {})
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

        const res = await oferenteBuscarPuestos(params)
        setResultados(res || [])
        setBuscado(true)
    }

    const limpiar = () => {
        setSeleccionadas([])
        setResultados([])
        setBuscado(false)
    }

    return (
        <div className="container-wide buscar-puestos-page">
            <section className="buscar-hero">
                <div>
                    <span className="buscar-etiqueta">Bolsa de empleo</span>
                    <h2>Buscar Puestos Disponibles</h2>
                    <p>
                        Filtra los puestos según tus habilidades y encuentra opciones que se ajusten mejor a tu perfil.
                    </p>
                </div>

                <div className="buscar-resumen">
                    <strong>{seleccionadas.length}</strong>
                    <span>habilidades seleccionadas</span>
                </div>
            </section>

            <section className="filtros-premium">
                <div className="filtros-premium-header">
                    <div>
                        <h3>Selecciona tus habilidades</h3>
                        <p>Escoge una o varias características para mejorar la búsqueda.</p>
                    </div>
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
                        <span>Define cómo se aplicarán los filtros seleccionados.</span>
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
                    <p className="empty">No se encontraron puestos.</p>
                )}

                {resultados.map((p) => (
                    <div key={p.id} className="puesto-result-card">
                        <div className="puesto-result-header">
                            <div className="puesto-result-info">
                                <div className="puesto-result-top">
                                    <h3 className="puesto-result-empresa">
                                        {p.empresa.nombre}
                                    </h3>

                                    <span
                                        className={`puesto-result-tipo ${
                                            p.tipo === 'PRIVADO' ? 'privado' : 'publico'
                                        }`}
                                    >
                    {p.tipo}
                  </span>
                                </div>

                                <p className="puesto-result-desc">
                                    {p.descripcion.substring(0, 80)}…
                                </p>

                                <div className="puesto-result-salario">
                                    {p.moneda.codigo} {Number(p.salario).toLocaleString('es-CR')}
                                </div>
                            </div>

                            <div className="puesto-result-match">
                                <div
                                    className={`match-number ${
                                        p.porcentajeCoincidencia >= 70
                                            ? 'alto'
                                            : p.porcentajeCoincidencia >= 40
                                                ? 'medio'
                                                : 'bajo'
                                    }`}
                                >
                                    {p.porcentajeCoincidencia.toFixed(1)}%
                                </div>

                                <div className="match-label">match</div>

                                <div className="match-detail">
                                    {p.requisitosCumplidos}/{p.requisitosTotal} cumplidos
                                </div>
                            </div>
                        </div>

                        <div className="puesto-result-barra">
                            <BarraCoincidencia pct={p.porcentajeCoincidencia} />
                        </div>

                        {p.coincidencias && p.coincidencias.length > 0 && (
                            <div className="puesto-result-tags">
                                {p.coincidencias.map((d) => (
                                    <span key={d.caracteristica} className="coincidencia-tag">
                    {estadoTexto[d.estado]} {d.caracteristica}{' '}
                                        {d.nivelOferente > 0
                                            ? `(${d.nivelOferente}→${d.nivelRequerido})`
                                            : '(sin habilidad)'}
                  </span>
                                ))}
                            </div>
                        )}
                    </div>
                ))}
            </section>
        </div>
    )
}