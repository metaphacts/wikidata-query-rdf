#!/bin/bash
DUMP_LIST=${DUMP_LIST:-"https://noc.wikimedia.org/conf/dblists/categories-rdf.dblist"}
DIR=${DIR:-`dirname $0`}
COMMAND="$1"

if [ -f $DUMP_LIST ]; then
	fetch="cat"
else
	fetch="curl -s -f -XGET"
fi

shift
$fetch $DUMP_LIST | while read wiki; do
	echo "Processing $wiki..."
	$DIR/$COMMAND $wiki "$@"
done
