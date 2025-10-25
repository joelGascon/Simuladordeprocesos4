/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nucleo;

import modelo.*;
import gestor.*;
import planificadores.*;
import persistencia.GestorConfiguracion;
import persistencia.Logger;

public class NucleoSistema {
    private static NucleoSistema instancia;
    
    private GestorColas gestorColas;
    private GestorHilos gestorHilos;
    private GestorExcepciones gestorExcepciones;
    private GestorMemoria gestorMemoria;
    private Planificador planificadorActual;
    private Configuracion configuracion;
    private Metricas metricas;
    private Reloj reloj;
    
    private Proceso procesoEjecutando;
    private int cicloActual;
    private boolean ejecutandoSO;
    private boolean simulacionActiva;
    private int contadorQuantum;
    
    private NucleoSistema() {
        this.configuracion = new Configuracion();
        this.gestorColas = new GestorColas();
        this.gestorHilos = new GestorHilos();
        this.gestorExcepciones = new GestorExcepciones();
        this.gestorMemoria = new GestorMemoria(configuracion.getMaxProcesosMemoria());
        this.metricas = new Metricas();
        this.cicloActual = 0;
        this.ejecutandoSO = false;
        this.simulacionActiva = false;
        this.contadorQuantum = 0;
        
        cargarConfiguracion();
        cambiarPlanificador(configuracion.getTipoPlanificador());
        
        this.reloj = new Reloj(this, configuracion.getDuracionCiclo());
    }
    
    public static NucleoSistema getInstance() {
        if (instancia == null) {
            instancia = new NucleoSistema();
        }
        return instancia;
    }
    
    public void iniciarSimulacion() {
        simulacionActiva = true;
        reloj.iniciar();
        Logger.getInstance().logEvento(new Evento("SISTEMA", "Simulación iniciada", null));
    }
    
    public void pausarSimulacion() {
        simulacionActiva = false;
        Logger.getInstance().logEvento(new Evento("SISTEMA", "Simulación pausada", null));
    }
    
    public void reanudarSimulacion() {
        simulacionActiva = true;
        Logger.getInstance().logEvento(new Evento("SISTEMA", "Simulación reanudada", null));
    }
    
    public void ejecutarCiclo() {
        if (!simulacionActiva) return;
        
        cicloActual++;
        ejecutandoSO = true;
        
        gestorColas.actualizarTiemposEspera();
        
        // Seleccionar siguiente proceso a ejecutar
        if (procesoEjecutando == null || procesoEjecutando.getEstado() != EstadoProceso.EJECUTANDO) {
            procesoEjecutando = planificadorActual.seleccionarSiguiente();
            contadorQuantum = 0;
        }
        
        // Ejecutar proceso actual
        if (procesoEjecutando != null) {
            ejecutarProcesoActual();
        } else {
            // CPU ociosa
            metricas.actualizarMetricas(cicloActual, 1, 
                gestorColas.getProcesosTerminados().size(), 
                calcularTiempoRespuestaPromedio());
        }
        
        ejecutandoSO = false;
        planificadorActual.ejecutarCiclo();
    }
    
    private void ejecutarProcesoActual() {
        try {
            procesoEjecutando.ejecutarInstruccion();
            contadorQuantum++;
            
            // Verificar si genera excepción de E/S
            if (procesoEjecutando.debeGenerarExcepcion()) {
                gestorColas.bloquearProceso(procesoEjecutando);
                
                if (gestorExcepciones.isActivo()) {
                    gestorExcepciones.manejarExcepcionIO(procesoEjecutando);
                }
                
                procesoEjecutando = null;
                contadorQuantum = 0;
                return;
            }
            
            // Verificar si terminó
            if (procesoEjecutando.estaTerminado()) {
                gestorColas.terminarProceso(procesoEjecutando);
                procesoEjecutando = null;
                contadorQuantum = 0;
            }
            
            // Manejo especial para planificadores multinivel
            manejarPlanificadoresEspeciales();
            
            // Actualizar métricas
            metricas.actualizarMetricas(cicloActual, 0, 
                gestorColas.getProcesosTerminados().size(), 
                calcularTiempoRespuestaPromedio());
                
        } catch (Exception e) {
            System.err.println("Error ejecutando proceso actual: " + e.getMessage());
            if (procesoEjecutando != null) {
                procesoEjecutando.setEstado(EstadoProceso.LISTO);
                gestorColas.agregarProceso(procesoEjecutando);
                procesoEjecutando = null;
                contadorQuantum = 0;
            }
        }
    }
    
    private void manejarPlanificadoresEspeciales() {
        // Manejo para Multinivel
        if (planificadorActual instanceof MultinivelPlanificador) {
            MultinivelPlanificador ml = (MultinivelPlanificador) planificadorActual;
            int nivel = (procesoEjecutando != null) ? procesoEjecutando.getPrioridad() : 0;
            int quantumNivel = ml.getQuantumParaNivel(nivel);
            
            if (quantumNivel > 0 && contadorQuantum >= quantumNivel && !procesoEjecutando.estaTerminado()) {
                ml.devolverProceso(procesoEjecutando, false);
                procesoEjecutando = null;
                contadorQuantum = 0;
            }
        }
    }
    
    private double calcularTiempoRespuestaPromedio() {
        int totalProcesos = gestorColas.getTodosProcesos().size();
        if (totalProcesos == 0) return 0;
        
        double sumaTiempos = 0;
        for (Proceso p : gestorColas.getTodosProcesos()) {
            sumaTiempos += p.getTiempoEspera();
        }
        return sumaTiempos / totalProcesos;
    }
    
    public void cambiarPlanificador(String tipo) {
        // Reiniciar contadores
        procesoEjecutando = null;
        contadorQuantum = 0;
        
        switch (tipo.toUpperCase()) {
            case "FCFS":
                planificadorActual = new FCFSPlanificador(gestorColas);
                break;
            case "SPN":
            case "SJF":
                planificadorActual = new SJFPlanificador(gestorColas);
                break;
            case "SRT":
                planificadorActual = new SRTPlanificador(gestorColas);
                break;
            case "ROUNDROBIN":
            case "RR":
                planificadorActual = new RoundRobinPlanificador(gestorColas, configuracion.getQuantumRR());
                break;
            case "HRRN":
                planificadorActual = new HRRNPlanificador(gestorColas);
                break;
            case "PRIORIDADES":
                planificadorActual = new PrioridadesPlanificador(gestorColas);
                break;
            case "MULTINIVEL":
                planificadorActual = new MultinivelPlanificador(gestorColas);
                break;
            case "MULTINIVEL_REALIMENTACION":
                planificadorActual = new MultinivelRealimentacionPlanificador(gestorColas);               break;
            default:
                planificadorActual = new FCFSPlanificador(gestorColas);
        }
        configuracion.setTipoPlanificador(tipo);
        Logger.getInstance().logEvento(new Evento("SISTEMA", 
            "Cambiado planificador a: " + tipo, null));
    }
    
    public void agregarProceso(Proceso proceso) {
        if (gestorMemoria.puedeCargarProceso()) {
            gestorMemoria.cargarProceso(proceso);
            gestorColas.agregarProceso(proceso);
            planificadorActual.agregarProceso(proceso);
        } else {
            Proceso procesoSuspender = gestorMemoria.seleccionarProcesoSuspender();
            if (procesoSuspender != null) {
                gestorColas.suspenderProceso(procesoSuspender);
                gestorMemoria.descargarProceso(procesoSuspender);
                gestorMemoria.cargarProceso(proceso);
                gestorColas.agregarProceso(proceso);
                planificadorActual.agregarProceso(proceso);
            }
        }
    }
    
    private void cargarConfiguracion() {
        GestorConfiguracion gestorConfig = new GestorConfiguracion();
        Configuracion configCargada = gestorConfig.cargarConfiguracion("config/config.json");
        if (configCargada != null) {
            this.configuracion = configCargada;
        }
    }
    
    public void guardarConfiguracion() {
        GestorConfiguracion gestorConfig = new GestorConfiguracion();
        gestorConfig.guardarConfiguracion(configuracion, "config/config.json");
    }
    
    // Getters
    public GestorColas getGestorColas() { return gestorColas; }
    public Planificador getPlanificadorActual() { return planificadorActual; }
    public Configuracion getConfiguracion() { return configuracion; }
    public Metricas getMetricas() { return metricas; }
    public Proceso getProcesoEjecutando() { return procesoEjecutando; }
    public int getCicloActual() { return cicloActual; }
    public boolean isEjecutandoSO() { return ejecutandoSO; }
    public boolean isSimulacionActiva() { return simulacionActiva; }
    public int getContadorQuantum() { return contadorQuantum; }
    
    // Setters
    public void setSimulacionActiva(boolean activa) { this.simulacionActiva = activa; }
    public void setDuracionCiclo(long duracion) { 
        configuracion.setDuracionCiclo(duracion);
        reloj.setDuracionCiclo(duracion);
    }
    
    public void shutdown() {
        simulacionActiva = false;
        
        // Detener el reloj primero
        if (reloj != null) {
            reloj.detener();
        }
        
        // Limpiar proceso actual
        if (procesoEjecutando != null) {
            procesoEjecutando.setEstado(EstadoProceso.LISTO);
            gestorColas.agregarProceso(procesoEjecutando);
            procesoEjecutando = null;
        }
        
        // Cerrar gestores en orden
        if (gestorExcepciones != null) {
            gestorExcepciones.shutdown();
        }
        
        if (gestorHilos != null) {
            gestorHilos.shutdown();
        }
        
        // Guardar configuración
        guardarConfiguracion();
        
        // Cerrar logger
        Logger.getInstance().close();
        
        Logger.getInstance().logEvento(new Evento("SISTEMA", "Sistema apagado correctamente", null));
    }
}