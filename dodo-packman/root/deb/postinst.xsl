<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="package">
    <xsl:text>#!/bin/bash
# -*- shell-script -*-
PREFIX=/</xsl:text>
    <xsl:value-of select="$prefix"/>
    <xsl:text>
function isFancyOutput { 

    if [ "x$TERM" != "xdumb" ] &amp;&amp; [ -x /usr/bin/tput ] &amp;&amp; tput hpa 60 &gt;/dev/null 2&gt;&amp;1; then
        GREEN=`tput setf 2`
        RED=`tput setf 4`
        YELLOW=`tput setf 6`
        NORM=`tput sgr0`
        CURSOR_INVISIBLE=`tput civis`
        CURSOR_VISIBLE=`tput cnorm`
        true
    else
        false
    fi
}

function progress {

  message=$1
  position=$2
  maximum=$3
  width=50

  line_end=" 100%"

  if [ ${position} -eq 0 ] ; then
      __current_position=0
      echo -n "${message} "
  fi

  if isFancyOutput ; then

      width=`tput cols`
      width=$(( ${width} - ${#message} - ${#line_end} - 3))

      if [ ${width} -lt 10 ] ; then
          width=10
      fi
  fi

  percent=$(( ${position} * ${width} / ${maximum} ))

  if [ ${percent} -gt ${__current_position} ] ; then

      while [ ${__current_position} -le ${percent} ] ; do
          echo -n "#"
          __current_position=$(( ${__current_position} + 1 ))
      done
  fi

  if [ ${position} -eq ${maximum} ] ; then
      echo " 100%"
  fi

}

index=1
</xsl:text>
    <xsl:apply-templates select="./postinstall"/>
    <xsl:text>
progress "Setting permissions:" 0 </xsl:text>
      <xsl:value-of select="count(./files/file)"/>
    <xsl:text>&#xa;</xsl:text>
    
    <xsl:apply-templates select="./files"/>
  </xsl:template>

  <xsl:template match="files">
    <xsl:variable name="max" select="count(./file)"/>
    <xsl:text>
</xsl:text>
    <xsl:for-each select="file">
      <xsl:variable name="recursive">
        <xsl:choose>
          <xsl:when test="@recursive='yes'">
            <xsl:text>-R</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text></xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:text>progress "Setting permissions:" $index </xsl:text> 
      <xsl:value-of select="$max"/>
      <xsl:text>&#xa;</xsl:text>
      <xsl:text>index=$(( $index + 1 ))&#xa;</xsl:text>
      <xsl:text>chown </xsl:text>
      <xsl:value-of select="$recursive"/>
      <xsl:text> </xsl:text>
      <xsl:value-of select="$user"/>
      <xsl:text>:</xsl:text>
      <xsl:value-of select="$group"/>
      <xsl:text> </xsl:text>
      <xsl:apply-templates select="path"/>
      <xsl:text>&#xa;</xsl:text>
      <xsl:text>chmod </xsl:text>
      <xsl:value-of select="$recursive"/>
      <xsl:text> </xsl:text>
      <xsl:value-of select="./mode"/>
      <xsl:text> </xsl:text>
      <xsl:apply-templates select="path"/>
      <xsl:text>&#xa;</xsl:text>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="prefix">
    <xsl:value-of select="'$PREFIX'"/>
  </xsl:template>

</xsl:stylesheet>
