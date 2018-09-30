#!/bin/bash

pushd . >/dev/null
cd ..
#sbt run
popd >/dev/null

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
					This page should update once a day...<br>
					Except it doesn'"'"'t yet, because I haven'"'"'t set up the part that downloads
					the new data from EDDB yet.
				</p>
				<h3>Columns</h3>
				<p>
					Each system in the pair is either the A system or the B system, and columns
					prefixed with A or B refer to that system.
				</p>
				<ul>
					<li>system_name: name of the system</li> <!-- duh -->
					<li>faction: major power aligned factions / total faction</li>
					<li>stations: number of stations in the system</li>
					<li>ls: light seconds to most distant station</li>
					<li>distance: distance between A and B systems</li>
				</ul>
				<h2>Federation</h2>
' > rank-grind.html

cat ../doc/elite-rank-grind-query.sql |\
	psql --dbname=elite --username=elite --host=127.0.0.1 --port=5432 -H |\
	grep -Ev '</?p>.*' |\
	sed 's/&nbsp;//g' |\
	sed 's/^/\t\t\t\t/' >> rank-grind.html

echo '				<h2>Empire</h2>' >> rank-grind.html

cat ../doc/elite-rank-grind-query.sql |\
	sed "s/'Federation'/'Empire'/" |\
	psql --dbname=elite --username=elite --host=127.0.0.1 --port=5432 -H |\
	grep -Ev '</?p>.*' |\
	sed 's/&nbsp;//g' |\
	sed 's/^/\t\t\t\t/' >> rank-grind.html

echo -n '			</main>
			<footer>
				<div class="line">
					<span>
						Last updated at '"$(date -u +%Y-%m-%dT%I:%M:%SZ)"'
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

# move the output into the apache directory
chgrp www-data rank-grind.html
chmod 640 rank-grind.html
mv rank-grind.html /var/www/
