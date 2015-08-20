<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="package">

<xsl:text>#!/bin/bash
set -x

function usage {
  echo "$1"
  exit 255
}

if [ "$PACKMAN_HOME" = "" ] ; then
  echo "Variable PACKMAN_HOME must be set!"
  exit 255
fi

DEST=""

while getopts "d:" arg
  do
  case $arg in
      d) DEST=$OPTARG;;
  esac
done

[ "${DEST}" = "" ] &amp;&amp; usage "You must specify the destination directory!"
install -d $DEST

BUILD=`pwd`/build

install -d $BUILD/spool

pkgmk -o -d $BUILD/spool -r /
[ $? -ne 0 ] &amp;&amp; exit 1

pkgtrans -o -s $BUILD/spool $DEST/</xsl:text>

<xsl:call-template name="pkgname"/>
<xsl:text>-</xsl:text>
<xsl:value-of select="/package/version"/>
<xsl:text>-</xsl:text>
<xsl:value-of select="/package/release"/>
<xsl:text>-sol8-sparc </xsl:text>
<xsl:call-template name="pkgname"/>

<xsl:text> 
[ $? -ne 0 ] &amp;&amp; exit 1

exit 0
</xsl:text>

</xsl:template>

</xsl:stylesheet>
