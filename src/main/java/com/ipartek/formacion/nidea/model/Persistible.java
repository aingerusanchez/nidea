package com.ipartek.formacion.nidea.model;

import java.util.ArrayList;

public interface Persistible<P> {

	/**
	 * Lista de una tabla de la BD ordenada por ID descendente y límitado a 500
	 * registros
	 * 
	 * @return Colección
	 */
	public ArrayList<P> getAll();

	/**
	 * Obtenemos el detalle de un registro
	 * 
	 * @param id
	 * @return SI EXISTE: Registros con id OTHERWISE null
	 */
	public P getById(int id);

	/**
	 * Guardamos un registro de la BD <br>
	 * IF Pojo.id == -1 : Creamos <br>
	 * IF Pojo.id != -1 : Modificamos
	 * 
	 * @param pojo
	 *            Objeto a guardar o modificar
	 * @return TRUE si lo ha guardado/modificado correctamente ELSE FALSE
	 */
	public boolean save(P pojo);

	/**
	 * Eliminamos un registro por su ID
	 * 
	 * @param id
	 *            identificado del registro a eliminar
	 * @return TRUE si lo ha eliminado correctamente ELSE FALSE
	 */
	public boolean delete(int id);

}