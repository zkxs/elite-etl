#!/bin/bash

findjar() {
	JARFILE="$(ls -1 target/scala-2.13/elite-etl-assembly-*.jar 2>/dev/null | sort -V | tail -n1)"
}

pushd . >/dev/null

cd "$(dirname "$(readlink -f "$0")")" # enter scripts directory

pushd . >/dev/null
cd .. # enter project root

findjar
if [ "$JARFILE" == "" ]; then
	sbt clean assembly
	findjar
fi
java -jar "$JARFILE"
STATUS=$?
popd >/dev/null # go back to scripts directory

if [ $STATUS -ne 0 ]; then
	echo "aborting script because elite-etl exited with status $STATUS"
	popd >/dev/null # go back to original directory
	exit 0
fi

export PGPASSWORD="$(cat ../config.properties | sed -n 's/.*&password=//p' | sed 's/\\ *$//')"

echo -n '<!DOCTYPE html>
<!-- m3ldrum so smexy -->
<html lang="en">
	<head>
		<meta charset="UTF-8" />
		<meta name="description" content="Up to date locations where there'"'"'s good major power rank grinding to be had" />
		<meta name="keywords" content="elite,dangerous,rank,grind,systems" />
		<meta name="author" content="Michael Ripley" />
		<title>Elite Dangerous Rank Grinding Locations</title>
		<link rel="stylesheet" type="text/css" href="/css/style.css">
	</head>
	<body>
		<div id="container">
			<main>
				<h1>Elite Dangerous Rank Grinding Locations</h1>
				<p>
					Finds pairs of systems that are suitable for major power rank grinding.
				</p>
				<p>
					This page <i>should</i> automatically update once a day. If it doesn'"'"'t, you should <a href="mailto:webmaster@michaelripley.net">let me know</a>.
				</p>
				<h3>Columns</h3>
				<p>
					Each system in the pair is either the A system or the B system, and columns
					prefixed with A or B refer to that system.
				</p>
				<ul>
					<li>system_name: name of the system</li> <!-- duh -->
					<li>faction: major power aligned factions / total factions in system</li>
					<li>boom_stations: boom stations / total stations in system</li>
					<li>any_large: does this system have any stations with large landing pads?</li>
					<li>ls: light seconds to most distant station</li>
					<li>distance: distance between A and B systems</li>
				</ul>
				<h2>Federation</h2>
' > rank-grind.html

# Run federation query
cat ../doc/elite-rank-grind-query.sql |\
	psql --dbname=elite --username=elite --host=127.0.0.1 --port=5432 -H |\
	grep -Ev '</?p>.*' |\
	sed 's/&nbsp;//g' |\
	sed 's/^/\t\t\t\t/' >> rank-grind.html

echo '				<h2>Empire</h2>' >> rank-grind.html

# Run empire query
cat ../doc/elite-rank-grind-query.sql |\
	sed "s/'Federation'/'Empire'/" |\
	psql --dbname=elite --username=elite --host=127.0.0.1 --port=5432 -H |\
	grep -Ev '</?p>.*' |\
	sed 's/&nbsp;//g' |\
	sed 's/^/\t\t\t\t/' >> rank-grind.html

DATE="$(date -u +%Y-%m-%dT%H:%M:%SZ)"

echo -n '				<h3>What systems are considered?</h3>
				<p>Only systems that meet the following critera make it in to the results:</p>
				<ul>
					<li>
						Systems A and B must be within [x] ly of each other.
						[x] ly is significant because it is the max distance data delivery missions will send you.
					</li>
					<li>
						No other system with dockable stations can be with [x] ly of A or B.
						This prevents delivery missions from sending you away from A or B.
					</li>
					<li>Systems must have at least one major power aligned faction.</li>
				</ul>
				<h3>Notes</h3>
				<ul>
					<li>Booms generate lots of good data delivery missions.</li>
					<li>Planetary outposts appear to have no effect, which is nice because ain'"'"'t nobody got time to land on those things.</li>
				</ul>
			</main>
			<footer>
				<div class="line">
					<span>
						Last updated at '"$DATE"'
					</span>
					<span class="divider"></span>
					<address class="validateLink" style="display: inline;">
						Webmaster: Michael Ripley (<a href="mailto:webmaster@michaelripley.net">email</a>)
					</address>
					<span class="divider"></span>
					<a href="https://github.com/zkxs/elite-etl">Source code</a>
				</div>
			</footer>
		</div>
	</body>
</html>
' >> rank-grind.html

echo "updated at $DATE"

# move the output into the apache directory
chgrp www-data rank-grind.html
chmod 640 rank-grind.html
mv rank-grind.html /var/www/

popd >/dev/null # go back to original directory
