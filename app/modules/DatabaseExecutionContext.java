package modules;

import play.libs.concurrent.CustomExecutionContext;
import akka.actor.ActorSystem;
import com.google.inject.Inject;

public class DatabaseExecutionContext extends CustomExecutionContext
{
	@Inject
	public DatabaseExecutionContext
	(
		ActorSystem actorSystem
	)
	{
		super(actorSystem, "database.dispatcher");
	}
}