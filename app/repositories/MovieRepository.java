package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlQuery;
import io.ebean.SqlRow;
import play.db.ebean.EbeanConfig;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;

import play.db.ebean.EbeanDynamicEvolutions;

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
}