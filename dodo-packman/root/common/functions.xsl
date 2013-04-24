<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" indent="yes"/>

  <xsl:template name="pkgname">
    <xsl:choose>
      <xsl:when test="string-length($pkgname) > 0">
        <xsl:value-of select="$pkgname"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="/package/name"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="filename">
    <xsl:param name="path" />
    <xsl:choose>
      <xsl:when test="contains($path, '/')">
         <xsl:call-template name="filename">
           <xsl:with-param name="path" select="substring-after($path, '/')" />
         </xsl:call-template>
       </xsl:when>
       <xsl:otherwise>
         <xsl:value-of select="$path" />
     </xsl:otherwise>
   </xsl:choose>
 </xsl:template>

  <xsl:template name="dirname">
    <xsl:param name="path" />
    <xsl:variable name="filename">
      <xsl:call-template name="filename">
        <xsl:with-param name="path" select="$path"/> 
      </xsl:call-template>
    </xsl:variable>
    <xsl:value-of select="substring($path, 1, string-length($path) - string-length($filename))"/>
 </xsl:template>

  <!-- ============================================  -->
  <!-- Standardvorlagen                              -->
  <!-- ============================================  -->

  <!-- install-dir
       Das Installationsverzeichnis ohne führenden /, 
       Z.B. usr/share/snl -->
  <xsl:template match="install-dir">
    <xsl:value-of select="$prefix"/>
  </xsl:template>

  <!-- buildroot
       Das Verzeichnis in dem die Software gebaut wird. Bei nicht relokatiblen
       Paketen wird direkt im Installationsverzeichnis gebaut. -->
  <xsl:template match="buildroot">
    <xsl:value-of select="$buildroot"/>
  </xsl:template>

  <xsl:template match="install-prefix">
    <xsl:value-of select="'$PREFIX'"/>
  </xsl:template>

  <!-- Das Installationsverzeichnis mit führenden /, 
       z.B. /usr/share/snl -->
  <xsl:template match="prefix">
    <xsl:text>/</xsl:text>
    <xsl:value-of select="$prefix"/>
  </xsl:template>

  <xsl:template match="relative-prefix">
    <xsl:value-of select="$prefix"/>
  </xsl:template>
    
  <!-- version 
       Die Versionsnummer des Paketes. -->
  <xsl:template match="version">
    <xsl:value-of select="/package/version"/>
  </xsl:template>

  <xsl:template match="user">
    <xsl:value-of select="$user"/>
  </xsl:template>

  <xsl:template match="group">
    <xsl:value-of select="$group"/>
  </xsl:template>
    
  <xsl:template match="application">
    <xsl:value-of select="$application"/>
  </xsl:template>

  <!-- Das bei der Installation gewählte Installationsverzeichnis. 
       Bei nicht relokatiblen Paketen wird prefix mit führendem / 
       verwendet -->
  <xsl:template match="install-prefix">
    <xsl:value-of select="'$PREFIX'"/>
  </xsl:template>

  <xsl:template match="jonas-base">
    <xsl:value-of select="'druckerpresse'"/>
  </xsl:template>

  <xsl:template match="path">
    <xsl:apply-templates/>
  </xsl:template>
    
</xsl:stylesheet>
