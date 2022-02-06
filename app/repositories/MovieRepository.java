package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlQuery;
import io.ebean.SqlRow;
import play.db.ebean.EbeanConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;

import play.db.ebean.EbeanDynamicEvolutions;
import requests.FilterRequest;
import responses.FilterResponse;

public class MovieRepository
{
	private final EbeanServer db;
	private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
	private final DatabaseExecutionContext databaseExecutionContext;

	@Inject
	public MovieRepository
	(
		EbeanConfig ebeanConfig,
		EbeanDynamicEvolutions ebeanDynamicEvolutions,
		DatabaseExecutionContext databaseExecutionContext
	)
	{
		this.ebeanDynamicEvolutions = ebeanDynamicEvolutions;
		this.db = Ebean.getServer(ebeanConfig.defaultServer());
		this.databaseExecutionContext = databaseExecutionContext;
	}

	public List<SqlRow> getDashboard()
	{
		List<SqlRow> dbList = new ArrayList<>();
		try
		{
			String sql = "select l.id as language_id, l.name as language, count(*) as count, sum(m.size) as size from movies m inner join languages l on m.language_id = l.id where m.active = 1 group by m.language_id";
			SqlQuery query = db.createSqlQuery(sql);
			dbList = query.findList();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
		return dbList;
	}

	public FilterResponse<Map<String, Object>> filter(FilterRequest filterRequest)
	{
		FilterResponse<Map<String, Object>> response = new FilterResponse<>();
		response.setOffset(filterRequest.getOffset());
		List<Map<String, Object>> forms = new ArrayList<>();

		String query = "select m.id as id, m.name as name, l.name as language, l.id as languageId, f.name as format, f.id as formatId, m.year as year, m.image_url as imageUrl from movies m " +
				"inner join languages l on l.id = m.language_id " +
				"inner join formats f on f.id = m.format_id";

		String countQuery = "select count(distinct m.id) as count from movies m " +
				"inner join languages l on l.id = m.language_id " +
				"inner join formats f on f.id = m.format_id";

		//where
		List<String> whereQueryParts = new ArrayList<>();

		whereQueryParts.add(getFieldNameWithTablePrefix("active") + " = 1");

		for(Map.Entry<String, List<String>> entry: filterRequest.getFilters().entrySet())
		{
			String field = entry.getKey();
			List<String> valueList = entry.getValue();

			String fieldNameWithTablePrefix = getFieldNameWithTablePrefix(field);
			if(!fieldNameWithTablePrefix.isEmpty() && !valueList.isEmpty())
			{
				whereQueryParts.add(fieldNameWithTablePrefix + " in (\"" + String.join(", ", valueList) + "\")");
			}
		}

//		for(Map.Entry<String, List<String>> entry: filterRequest.getFilters().entrySet())
//		{
//			String field = entry.getKey();
//			List<String> valueList = entry.getValue();
//
//			String fieldNameWithTablePrefix = getFieldNameWithTablePrefix(field);
//			if(!fieldNameWithTablePrefix.isEmpty() && !valueList.isEmpty())
//			{
//				whereQueryParts.add(fieldNameWithTablePrefix + " in (" + String.join(", ", valueList) + ")");
//			}
//		}

		for(Map.Entry<String, Boolean> entry: filterRequest.getBooleanFilters().entrySet())
		{
			String field = entry.getKey();
			Boolean value = entry.getValue();

			String fieldNameWithTablePrefix = getFieldNameWithTablePrefix(field);
			if(!fieldNameWithTablePrefix.isEmpty() && value != null)
			{
				whereQueryParts.add(fieldNameWithTablePrefix + " = " + value);
			}
		}

		if(!whereQueryParts.isEmpty())
		{
			query += " where " + String.join(" and ", whereQueryParts);
			countQuery += " where " + String.join(" and ", whereQueryParts);
		}

		//sort
		List<String> sortList = new ArrayList<>();
		for(Map.Entry<String, String> entry: filterRequest.getSortMap().entrySet())
		{
			String field = entry.getKey();
			String value = entry.getValue();

			String sortFieldName = getFieldNameForDisplay(field);
			if(!sortFieldName.isEmpty())
			{
				sortList.add(sortFieldName + " " + value);
			}
		}
		if(sortList.isEmpty())
		{
			sortList.add(getFieldNameForDisplay("year") + " desc");
			sortList.add(getFieldNameForDisplay("id") + " desc");
		}
		query += " order by " + String.join(", ", sortList);

		//offset limit
		query += " limit " + Integer.min(100, filterRequest.getCount()) + " offset " + filterRequest.getOffset();

		try
		{
			SqlQuery sqlCountQuery = this.db.createSqlQuery(countQuery);
			List<SqlRow> countResult = sqlCountQuery.findList();
			response.setTotalCount(countResult.get(0).getInteger("count"));

			SqlQuery sqlQuery = this.db.createSqlQuery(query);
			List<SqlRow> result = sqlQuery.findList();

			for(SqlRow row: result)
			{
				Map<String, Object> entry = new HashMap<>();
				entry.put("id", row.getLong("id"));
				entry.put("name", row.getString("name"));
				entry.put("language", row.getString("language"));
				entry.put("languageId", row.getInteger("languageId"));
				entry.put("format", row.getString("format"));
				entry.put("formatId", row.getInteger("formatId"));
				entry.put("imageUrl", row.getString("imageUrl"));
				entry.put("year", row.getInteger("year"));

				forms.add(entry);
			}
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		response.setList(forms);

		return response;
	}

	public String getFieldNameWithTablePrefix(String field)
	{
		String fieldName = "";

		switch(field)
		{
			case "language":
				fieldName = "m.language_id";
				break;
			case "format":
				fieldName = "m.format_id";
				break;
			case "active":
				fieldName = "m.active";
				break;
			case "quality":
				fieldName = "m.quality";
				break;
			case "subtitles":
				fieldName = "m.subtitles";
				break;
			case "seenInTheatre":
				fieldName = "m.seen_in_theatre";
				break;
		}

		return fieldName;
	}

	public String getFieldNameForDisplay(String field)
	{
		String fieldName = "";

		switch(field)
		{
			case "language":
				fieldName = "language";
				break;
			case "format":
				fieldName = "format";
				break;
			case "year":
				fieldName = "year";
				break;
			case "id":
				fieldName = "id";
				break;
			case "name":
				fieldName = "name";
				break;
		}

		return fieldName;
	}
}