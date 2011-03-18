/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.dynamicdatamapping.storage;

import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.dynamicdatamapping.StorageException;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * @author Eduardo Lundgren
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 */
public abstract class BaseStorageAdapter implements StorageAdapter {

	public long create(
			long companyId, long structureId, Map<String, Serializable> fields,
			ServiceContext serviceContext)
		throws StorageException {

		try {
			return doCreate(companyId, structureId, fields, serviceContext);
		}
		catch (StorageException se) {
			throw se;
		}
		catch (Exception e) {
			throw new StorageException(e);
		}
	}

	public void delete(long classPK) throws StorageException {
		try {
			doDelete(classPK);
		}
		catch (StorageException se) {
			throw se;
		}
		catch (Exception e) {
			throw new StorageException(e);
		}
	}

	public void deleteAll(long structureId) throws StorageException {
		try {
			doDeleteAll(structureId);
		}
		catch (StorageException se) {
			throw se;
		}
		catch (Exception e) {
			throw new StorageException(e);
		}
	}

	public List<Map<String, Serializable>> getAllFields(
			long structureId, List<String> fieldNames)
		throws StorageException {

		return getAllFields(structureId, fieldNames, null);
	}

	public Map<Long, List<Map<String, Serializable>>> getAllFields(
			long[] structureIds, List<String> fieldNames)
		throws StorageException {

		return getAllFields(structureIds, fieldNames, null);
	}

	public List<Map<String, Serializable>> getAllFields(
			long structureId, List<String> fieldNames, OrderByComparator obc)
		throws StorageException {

		Map<Long, List<Map<String, Serializable>>> results = getAllFields(
			new long[] {structureId}, fieldNames, obc);

		return results.get(structureId);
	}

	public Map<Long, List<Map<String, Serializable>>> getAllFields(
			long[] structureIds, List<String> fieldNames,
			OrderByComparator obc)
		throws StorageException {

		try {
			return doGetAllFields(structureIds, fieldNames, obc);
		}
		catch (StorageException se) {
			throw se;
		}
		catch (Exception e) {
			throw new StorageException(e);
		}
	}

	public Map<String, Serializable> getFields(long classPK)
		throws StorageException {

		return getFields(new long[] {classPK}).get(classPK);
	}

	public Map<String, Serializable> getFields(
			long classPK, List<String> fieldNames)
		throws StorageException {

		return getFields(new long[] {classPK}, fieldNames).get(classPK);
	}

	public Map<Long, Map<String, Serializable>> getFields(long[] classPKs)
		throws StorageException {

		return getFields(classPKs, (List<String>)null);
	}

	public Map<Long, Map<String, Serializable>> getFields(
			long[] classPKs, List<String> fieldNames)
		throws StorageException {

		try {
			return doGetFields(classPKs, fieldNames);
		}
		catch (StorageException se) {
			throw se;
		}
		catch (Exception e) {
			throw new StorageException(e);
		}
	}

	public List<Map<String, Serializable>> getFields(
			long[] classPKs, OrderByComparator obc)
		throws StorageException {

		return getFields(classPKs, null, obc);
	}

	public List<Map<String, Serializable>> getFields(
			long[] classPKs, List<String> fieldNames, OrderByComparator obc)
		throws StorageException {

		try {
			return doGetFields(classPKs, fieldNames, obc);
		}
		catch (StorageException se) {
			throw se;
		}
		catch (Exception e) {
			throw new StorageException(e);
		}
	}

	public List<Map<String, Serializable>> query(
			long structureId, List<String> fieldNames, String whereClause,
			OrderByComparator obc)
		throws StorageException {

		try {
			return doQuery(structureId, fieldNames, whereClause, obc);
		}
		catch (StorageException se) {
			throw se;
		}
		catch (Exception e) {
			throw new StorageException(e);
		}
	}

	public int queryCount(long structureId, String whereClause)
		throws StorageException {

		try {
			return doQueryCount(structureId, whereClause);
		}
		catch (StorageException se) {
			throw se;
		}
		catch (Exception e) {
			throw new StorageException(e);
		}
	}

	public void update(
			long classPK, Map<String, Serializable> fields,
			ServiceContext serviceContext)
		throws StorageException {

		update(classPK, fields, false, serviceContext);
	}

	public void update(
			long classPK, Map<String, Serializable> fields, boolean merge,
			ServiceContext serviceContext)
		throws StorageException {

		try {
			doUpdate(classPK, fields, merge, serviceContext);
		}
		catch (StorageException se) {
			throw se;
		}
		catch (Exception e) {
			throw new StorageException(e);
		}
	}

	protected abstract long doCreate(
			long companyId, long structureId, Map<String, Serializable> fields,
			ServiceContext serviceContext)
		throws Exception;

	protected abstract void doDelete(long classPK) throws Exception;

	protected abstract void doDeleteAll(long structureId) throws Exception;

	protected abstract Map<Long, List<Map<String, Serializable>>>
		doGetAllFields(
				long[] structureIds, List<String> fieldNames,
				OrderByComparator obc)
			throws Exception;

	protected abstract Map<Long, Map<String, Serializable>> doGetFields(
			long[] classPKs, List<String> fieldNames)
		throws Exception;

	protected abstract List<Map<String, Serializable>> doGetFields(
			long[] classPKs, List<String> fieldNames, OrderByComparator obc)
		throws Exception;

	protected abstract List<Map<String, Serializable>> doQuery(
			long structureId, List<String> fieldNames, String whereClause,
			OrderByComparator obc)
		throws Exception;

	protected abstract int doQueryCount(long structureId, String whereClause)
		throws Exception;

	protected abstract void doUpdate(
			long classPK, Map<String, Serializable> fields, boolean merge,
			ServiceContext serviceContext)
		throws Exception;

}