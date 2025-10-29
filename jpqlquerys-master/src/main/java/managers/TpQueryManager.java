package managers;

import org.example.Articulo;
import org.example.Cliente;
import org.example.Factura;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

/**
 * Esta clase contiene todos los métodos con las consultas JPQL
 * para resolver el Trabajo Práctico.
 */
public class TpQueryManager {

    private EntityManagerFactory emf;
    private EntityManager em;

    /**
     * Constructor que inicializa el EntityManager.
     * Utiliza la misma unidad de persistencia "example-unit" de tus archivos.
     *
     */
    public TpQueryManager() {
        // Usamos "false" para no ver el log de SQL de Hibernate y ver solo nuestros resultados.
        // (Similar a como lo haces en FacturaManager)
        this.emf = Persistence.createEntityManagerFactory("example-unit");
        this.em = emf.createEntityManager();
    }

    /**
     * Cierra la conexión del EntityManager.
     */
    public void cerrar() {
        if (em != null) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }

    // --- INICIO DE EJERCICIOS DEL TP ---

    /**
     * Ejercicio 1: Listar todos los clientes.
     *
     */
    public List<Cliente> ejercicio1_listarClientes() {
        // Seleccionamos la entidad completa 'Cliente'
        String jpql = "SELECT c FROM Cliente c ORDER BY c.razonSocial";
        TypedQuery<Cliente> query = em.createQuery(jpql, Cliente.class);
        return query.getResultList();
    }

    /**
     * Ejercicio 2: Listar todas las facturas generadas en el último mes.
     *
     */
    public List<Factura> ejercicio2_facturasUltimoMes() {
        LocalDate fechaDesde = LocalDate.now().minusMonths(1);
        // Filtramos por el campo 'fechaComprobante' de la entidad Factura
        String jpql = "SELECT f FROM Factura f WHERE f.fechaComprobante >= :fechaDesde ORDER BY f.fechaComprobante DESC";
        TypedQuery<Factura> query = em.createQuery(jpql, Factura.class);
        query.setParameter("fechaDesde", fechaDesde);
        return query.getResultList();
    }

    /**
     * Ejercicio 3: Obtener el cliente que ha generado más facturas.
     *
     */
    public Cliente ejercicio3_clienteConMasFacturas() {
        String jpql = "SELECT f.cliente FROM Factura f GROUP BY f.cliente ORDER BY COUNT(f) DESC";
        TypedQuery<Cliente> query = em.createQuery(jpql, Cliente.class);
        query.setMaxResults(1); // Nos quedamos solo con el primero
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null; // En caso de que no haya facturas
        }
    }

    /**
     * Ejercicio 4: Listar los artículos más vendidos.
     *
     * Retorna una lista de Object[], donde:
     * Object[0] = Articulo (entidad)
     * Object[1] = Double (total vendido)
     */
    public List<Object[]> ejercicio4_articulosMasVendidos() {
        // Usamos JOIN implícito, agrupamos por artículo y sumamos la 'cantidad'
        String jpql = "SELECT fd.articulo, SUM(fd.cantidad) AS totalVendido " +
                "FROM FacturaDetalle fd " +
                "GROUP BY fd.articulo " +
                "ORDER BY totalVendido DESC";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        return query.getResultList();
    }

    /**
     * Ejercicio 5: Facturas emitidas en los 3 últimos meses de un cliente específico.
     *
     */
    public List<Factura> ejercicio5_facturasClienteUltimos3Meses(Long idCliente) {
        LocalDate fechaDesde = LocalDate.now().minusMonths(3);
        String jpql = "SELECT f FROM Factura f WHERE f.cliente.id = :idCliente AND f.fechaComprobante >= :fechaDesde";
        TypedQuery<Factura> query = em.createQuery(jpql, Factura.class);
        query.setParameter("idCliente", idCliente);
        query.setParameter("fechaDesde", fechaDesde);
        return query.getResultList();
    }

    /**
     * Ejercicio 6: Calcular el monto total facturado por un cliente específico.
     *
     */
    public Double ejercicio6_totalFacturadoCliente(Long idCliente) {
        // Sumamos el campo 'total' de la entidad Factura
        String jpql = "SELECT SUM(f.total) FROM Factura f WHERE f.cliente.id = :idCliente";
        TypedQuery<Double> query = em.createQuery(jpql, Double.class);
        query.setParameter("idCliente", idCliente);
        Double resultado = query.getSingleResult();
        return (resultado != null) ? resultado : 0.0;
    }

    /**
     * Ejercicio 7: Listar los Artículos vendidos en una factura específica.
     *
     */
    public List<Articulo> ejercicio7_articulosDeFactura(Long idFactura) {
        // Navegamos desde FacturaDetalle hasta Articulo
        String jpql = "SELECT fd.articulo FROM FacturaDetalle fd WHERE fd.factura.id = :idFactura";
        TypedQuery<Articulo> query = em.createQuery(jpql, Articulo.class);
        query.setParameter("idFactura", idFactura);
        return query.getResultList();
    }

    /**
     * Ejercicio 8: Obtener el Artículo más caro vendido en una factura específica.
     *
     */
    /**
     * Ejercicio 8: Obtener el Artículo más caro vendido en una factura específica.
     *
     */
    public Articulo ejercicio8_articuloMasCaroFactura(Long idFactura) {
        // CORRECCIÓN:
        // La consulta original (ORDER BY fd.articulo.precioVenta) puede fallar
        // en algunas versiones de Hibernate con herencia.
        // Es más seguro seleccionar el precio, ordenarlo, y luego tomar el artículo.
        String jpql = "SELECT fd.articulo, fd.articulo.precioVenta AS precio " +
                "FROM FacturaDetalle fd " +
                "WHERE fd.factura.id = :idFactura " +
                "ORDER BY precio DESC";

        // La consulta ahora devuelve Object[] (Articulo, Double)
        // Nota: Puede que necesites importar javax.persistence.TypedQuery si no está
        javax.persistence.TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("idFactura", idFactura);
        query.setMaxResults(1); // Tomamos solo el primero

        List<Object[]> resultados = query.getResultList();

        if (resultados.isEmpty()) {
            return null; // Si la factura no tiene artículos
        } else {
            return (Articulo) resultados.get(0)[0]; // Devolvemos el Articulo (índice 0)
        }
    }

    /**
     * Ejercicio 9: Contar la cantidad total de facturas generadas.
     *
     */
    public Long ejercicio9_contarFacturas() {
        String jpql = "SELECT COUNT(f) FROM Factura f";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        return query.getSingleResult();
    }

    /**
     * Ejercicio 10: Listar las facturas cuyo total es mayor a un valor determinado.
     *
     */
    public List<Factura> ejercicio10_facturasMayorA(Double montoMinimo) {
        String jpql = "SELECT f FROM Factura f WHERE f.total > :montoMinimo";
        TypedQuery<Factura> query = em.createQuery(jpql, Factura.class);
        query.setParameter("montoMinimo", montoMinimo);
        return query.getResultList();
    }

    /**
     * Ejercicio 11: Facturas que contienen un Artículo específico (por nombre).
     *
     */
    public List<Factura> ejercicio11_facturasConArticulo(String nombreArticulo) {
        // Usamos DISTINCT para no repetir facturas si tienen el mismo artículo varias veces
        // Hacemos JOIN con la colección 'detallesFactura'
        String jpql = "SELECT DISTINCT f FROM Factura f JOIN f.detallesFactura fd " +
                "WHERE fd.articulo.denominacion = :nombreArticulo";
        TypedQuery<Factura> query = em.createQuery(jpql, Factura.class);
        query.setParameter("nombreArticulo", nombreArticulo);
        return query.getResultList();
    }

    /**
     * Ejercicio 12: Listar Artículos filtrando por código parcial.
     *
     */
    public List<Articulo> ejercicio12_articulosPorCodigo(String codigoParcial) {
        // Usamos LIKE y wildcards (%) para buscar coincidencias parciales en 'codigo'
        String jpql = "SELECT a FROM Articulo a WHERE a.codigo LIKE :codigoParcial";
        TypedQuery<Articulo> query = em.createQuery(jpql, Articulo.class);
        query.setParameter("codigoParcial", "%" + codigoParcial + "%");
        return query.getResultList();
    }

    /**
     * Ejercicio 13: Artículos cuyo precio sea mayor que el promedio.
     *
     */
    public List<Articulo> ejercicio13_articulosPrecioMayorPromedio() {
        // Usamos una subconsulta para calcular el AVG (promedio) de 'precioVenta'
        String jpql = "SELECT a FROM Articulo a " +
                "WHERE a.precioVenta > (SELECT AVG(a2.precioVenta) FROM Articulo a2)";
        TypedQuery<Articulo> query = em.createQuery(jpql, Articulo.class);
        return query.getResultList();
    }

    /**
     * Ejercicio 14: Ejemplo de la cláusula EXISTS.
     *
     * (Listar clientes que SÍ tengan al menos una factura)
     */
    public List<Cliente> ejercicio14_clientesConFacturas() {
        // EXISTS es true si la subconsulta devuelve al menos una fila.
        String jpql = "SELECT c FROM Cliente c " +
                "WHERE EXISTS (SELECT f FROM Factura f WHERE f.cliente = c)";
        TypedQuery<Cliente> query = em.createQuery(jpql, Cliente.class);
        return query.getResultList();
    }
}