if test -f "dist/RUNNING_PID"
then
	echo "Stopping My Api Java Server";
	kill -9 $(cat dist/RUNNING_PID);
	rm dist/RUNNING_PID;
fi
