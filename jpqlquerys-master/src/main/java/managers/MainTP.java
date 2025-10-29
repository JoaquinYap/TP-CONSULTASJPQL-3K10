package managers;

import org.example.Articulo;
import org.example.Cliente;
import org.example.Factura;

import java.util.List;

/**
 * Clase principal para ejecutar y mostrar los resultados del Trabajo Práctico de JPQL.
 */
public class MainTP {

    public static void main(String[] args) {
        TpQueryManager tpManager = new TpQueryManager();

        try {
            System.out.println("--- Ejercicio 1: Listar todos los clientes ---");
            List<Cliente> clientes1 = tpManager.ejercicio1_listarClientes();
            for (Cliente c : clientes1) {
                System.out.println("  > " + c.getRazonSocial() + " (ID: " + c.getId() + ")");
            }

            System.out.println("\n--- Ejercicio 2: Listar facturas del último mes ---");
            List<Factura> facturas2 = tpManager.ejercicio2_facturasUltimoMes();
            for (Factura f : facturas2) {
                System.out.println("  > Factura N° " + f.getNroComprobante() + " (Fecha: " + f.getFechaComprobante() + ")");
            }

            System.out.println("\n--- Ejercicio 3: Cliente con más facturas ---");
            Cliente cliente3 = tpManager.ejercicio3_clienteConMasFacturas();
            if (cliente3 != null) {
                System.out.println("  > El cliente es: " + cliente3.getRazonSocial());
            }

            System.out.println("\n--- Ejercicio 4: Artículos más vendidos ---");
            List<Object[]> articulos4 = tpManager.ejercicio4_articulosMasVendidos();
            for (Object[] obj : articulos4) {
                Articulo art = (Articulo) obj[0];
                // El SUM(fd.cantidad) puede devolver Long o Double dependiendo del motor de BD.
                // Lo casteamos a Number y luego lo convertimos a Double para seguridad.
                Double total = ((Number) obj[1]).doubleValue();
                System.out.println("  > " + art.getDenominacion() + " - Total Vendido: " + total);
            }

            System.out.println("\n--- Ejercicio 5: Facturas de un cliente (ID=1) en últimos 3 meses ---");
            // Nota: Usamos ID=1L como ejemplo, cámbialo si es necesario.
            List<Factura> facturas5 = tpManager.ejercicio5_facturasClienteUltimos3Meses(1L);
            for (Factura f : facturas5) {
                System.out.println("  > Factura N° " + f.getNroComprobante() + " (Cliente ID: " + f.getCliente().getId() + ")");
            }

            System.out.println("\n--- Ejercicio 6: Monto total facturado por un cliente (ID=1) ---");
            // Nota: Usamos ID=1L como ejemplo.
            Double total6 = tpManager.ejercicio6_totalFacturadoCliente(1L);
            System.out.println("  > Total facturado al cliente 1: $" + total6);

            System.out.println("\n--- Ejercicio 7: Artículos vendidos en una factura (ID=1) ---");
            // Nota: Usamos ID=1L como ejemplo.
            List<Articulo> articulos7 = tpManager.ejercicio7_articulosDeFactura(1L);
            for (Articulo a : articulos7) {
                System.out.println("  > " + a.getDenominacion());
            }

            System.out.println("\n--- Ejercicio 8: Artículo más caro en una factura (ID=1) ---");
            // Nota: Usamos ID=1L como ejemplo.
            Articulo articulo8 = tpManager.ejercicio8_articuloMasCaroFactura(1L);
            if (articulo8 != null) {
                System.out.println("  > " + articulo8.getDenominacion() + " ($" + articulo8.getPrecioVenta() + ")");
            }

            System.out.println("\n--- Ejercicio 9: Contar total de facturas ---");
            Long total9 = tpManager.ejercicio9_contarFacturas();
            System.out.println("  > Hay un total de " + total9 + " facturas.");

            System.out.println("\n--- Ejercicio 10: Facturas con total mayor a $100 ---");
            // Nota: Usamos 100.0 como ejemplo.
            List<Factura> facturas10 = tpManager.ejercicio10_facturasMayorA(100.0);
            for (Factura f : facturas10) {
                System.out.println("  > Factura N° " + f.getNroComprobante() + " (Total: $" + f.getTotal() + ")");
            }

            System.out.println("\n--- Ejercicio 11: Facturas que contienen 'Manzana' ---");
            // Nota: Usamos 'Manzana' como ejemplo
            List<Factura> facturas11 = tpManager.ejercicio11_facturasConArticulo("Manzana");
            for (Factura f : facturas11) {
                System.out.println("  > Factura N° " + f.getNroComprobante());
            }

            System.out.println("\n--- Ejercicio 12: Artículos por código parcial (ej: '123') ---");
            // Nota: Usamos "123" como ejemplo.
            List<Articulo> articulos12 = tpManager.ejercicio12_articulosPorCodigo("a");
            for (Articulo a : articulos12) {
                System.out.println("  > " + a.getDenominacion() + " (Código: " + a.getCodigo() + ")");
            }

            System.out.println("\n--- Ejercicio 13: Artículos con precio mayor al promedio ---");
            List<Articulo> articulos13 = tpManager.ejercicio13_articulosPrecioMayorPromedio();
            for (Articulo a : articulos13) {
                System.out.println("  > " + a.getDenominacion() + " ($" + a.getPrecioVenta() + ")");
            }

            System.out.println("\n--- Ejercicio 14: Clientes con al menos una factura (EXISTS) ---");
            System.out.println("  (La cláusula EXISTS se usa para verificar si una subconsulta " +
                    "devuelve al menos una fila. Es más eficiente que un COUNT > 0 " +
                    "porque se detiene en cuanto encuentra la primera coincidencia).");
            List<Cliente> clientes14 = tpManager.ejercicio14_clientesConFacturas();
            for (Cliente c : clientes14) {
                System.out.println("  > " + c.getRazonSocial() + " tiene facturas.");
            }

        } catch (Exception e) {
            System.out.println("Ocurrió un error al ejecutar las consultas:");
            e.printStackTrace();
        } finally {
            // Cerramos la conexión al finalizar
            tpManager.cerrar();
            System.out.println("\n--- Conexión cerrada. Fin del TP. ---");
        }
    }
}