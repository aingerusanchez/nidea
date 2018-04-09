package com.ipartek.formacion.nidea.controller.backoffice;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ipartek.formacion.nidea.model.MaterialDAO;
import com.ipartek.formacion.nidea.pojo.Alert;
import com.ipartek.formacion.nidea.pojo.Material;

/**
 * Servlet implementation class MaterialesController
 */
@WebServlet(description = "Controller para mostrar los materiales en backoffice", urlPatterns = {
		"/backoffice/materiales" })
public class MaterialesBOController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String VIEW_INDEX = "materiales/index.jsp";
	private static final String VIEW_FORM = "materiales/material-detalle.jsp";
	public static final int OP_MOSTRAR_FORM = 1;
	public static final int OP_BUSQUEDA = 2;
	public static final int OP_ELIMINAR = 3;
	public static final int OP_GUARDAR = 4;

	// private RequestDispatcher dispatcher;
	private String view = VIEW_INDEX;;
	private Alert alert = null;
	private MaterialDAO dao = null;

	// Parámetros comunes
	private String search; // Termino para el filtro de búsqueda
	private int op = -1;

	// Parámetros de Material
	private int id;
	private String nombre;
	private float precio;

	/**
	 * Se ejecuta únicamente la primera vez que se llama al Servlet
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		dao = MaterialDAO.getInstance();
	}

	/**
	 * Se ejecuta sólo cuando paramos el servidor
	 */
	@Override
	public void destroy() {
		super.destroy();
		dao = null;
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Código que se ejcecuta ANTES de hacer la llamada a doGET o doPOST
		super.service(request, response);
		// Código que se ejcecuta DESPUÉS de hacer la llamada a doGET o doPOST
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doProcess(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Método para englobar los métodos doGet y doPost
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doProcess(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {

			ArrayList<Material> materiales = null;
			alert = null /* new Alert("Lista de Materiales para Admin", Alert.TIPO_PRIMARY) */;
			recogerParametros(request);

			switch (op) {
			case OP_MOSTRAR_FORM:
				mostraFormulario(request);
				break;
			case OP_ELIMINAR:
				eliminar(request, materiales);
				break;
			case OP_BUSQUEDA:
				buscar(request, materiales);
				break;
			case OP_GUARDAR:
				guardar(request, materiales);
				break;
			default:
				listar(request, materiales);
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			view = VIEW_INDEX;
			alert = new Alert();

		} finally {
			request.setAttribute("alert", alert);
			request.getRequestDispatcher(view).forward(request, response);
		}
	}

	/**
	 * Recogemos todos los parámetros de la request
	 * 
	 * @param request
	 */
	private void recogerParametros(HttpServletRequest request) {
		if (request.getParameter("op") != null) {
			op = Integer.parseInt(request.getParameter("op"));
		}

		search = (request.getParameter("search") != null) ? request.getParameter("search") : "";
		// System.out.println("Filtro de busqueda: " + search);

		if (request.getParameter("id") != null) {
			id = Integer.parseInt(request.getParameter("id"));
		}
		if (request.getParameter("nombre") != null) {
			nombre = request.getParameter("nombre");
		}
		if (request.getParameter("precio") != null) {
			precio = Float.parseFloat(request.getParameter("precio"));
		}
	}

	private void listar(HttpServletRequest request, ArrayList<Material> materiales) {

		materiales = dao.getAll();
		request.setAttribute("materiales", materiales);
		view = VIEW_INDEX;

	}

	private void guardar(HttpServletRequest request, ArrayList<Material> materiales) {

		cargarInfoMaterial(request);
		Material material = new Material();
		material.setId(id);
		material.setNombre(nombre);
		material.setPrecio(precio);

		if (id == -1) {
			// Crear nuevo material
			if (dao.save(material)) {
				alert = new Alert("Nuevo material " + nombre + " guardado correctamente", Alert.TIPO_SUCCESS);
			} else {
				alert = new Alert("Error al crear el material " + nombre + " con id " + id, Alert.TIPO_WARNING);
			}

		} else {
			// Modificar material
			if (dao.save(material)) {
				alert = new Alert("Material con id " + id + " modificado correctamente", Alert.TIPO_SUCCESS);
			} else {
				alert = new Alert("Error al modificar el material " + nombre + " con id " + id, Alert.TIPO_WARNING);
			}
		}

		listar(request, materiales);

	}

	private void buscar(HttpServletRequest request, ArrayList<Material> materiales) {

		if (search != "") {
			materiales = dao.filterMateriales(search);
		} else {
			// devolver la lista completa de materiales 'getAll()'
			materiales = dao.getAll();
		}
		request.setAttribute("materiales", materiales);
		view = VIEW_INDEX;
	}

	private void eliminar(HttpServletRequest request, ArrayList<Material> materiales) {

		cargarInfoMaterial(request);
		if (dao.delete(id)) {
			alert = new Alert("El material " + nombre + " con id " + id + " ha sido eliminado correctamente",
					Alert.TIPO_DANGER);
		} else {
			alert = new Alert("Error al eliminar el material " + nombre + " con id " + id, Alert.TIPO_WARNING);
		}

		listar(request, materiales);

	}

	private void mostraFormulario(HttpServletRequest request) {

		cargarInfoMaterial(request);

		if (id == -1) {
			// Crear nuevo material
			alert = new Alert("Nuevo material a crear", Alert.TIPO_SUCCESS);
		} else {
			// Modificar material
			alert = new Alert("Detalle del material con id " + id + " a consultar/modificar/eliminar",
					Alert.TIPO_PRIMARY);
		}

		view = VIEW_FORM;
	}

	private void cargarInfoMaterial(HttpServletRequest request) {

		Material material = new Material();
		if (id > -1) {
			material.setId(id);
			material.setNombre(nombre);
			material.setPrecio(precio);
		}
		request.setAttribute("material", material);

	}

}