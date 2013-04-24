<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" indent="yes"/>
  
  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="package">
<xsl:text>
#
# RPM Spec File
      
Name: </xsl:text><xsl:call-template name="pkgname"/>
Version:<xsl:value-of select="/package/version"/>
Release:<xsl:value-of select="/package/release"/>
Summary:<xsl:value-of select="/package/summary"/>
Group:<xsl:value-of select="/package/group"/>
<xsl:apply-templates select="/package/copyright"/>
<xsl:apply-templates select="/package/license"/>
BuildArchitectures: <xsl:apply-templates select="architectures"/>
<xsl:if test="/package/@relocatable='yes'">
Prefix: /<xsl:value-of select="$prefix"/>
BuildRoot: <xsl:value-of select="$buildroot"/>
</xsl:if>

<xsl:apply-templates select="archives"/>

<xsl:apply-templates select="/package/provides"/>
<xsl:apply-templates select="/package/requires"/>
<xsl:apply-templates select="/package/autoreqprov"/>

%description
<xsl:value-of select="/package/description"/>

%prep
<xsl:apply-templates select="archives" mode="extract"/>
<xsl:apply-templates select="prepare"/>

%build
<xsl:apply-templates select="build"/>

%install
<xsl:apply-templates select="install"/>

%pre
<xsl:apply-templates select="preinstall"/>

%preun
<xsl:apply-templates select="preuninstall"/>

%post
<xsl:apply-templates select="postinstall"/>

%postun
<xsl:apply-templates select="postuninstall"/>

%files
<xsl:apply-templates select="files"/>

  </xsl:template>
    
  <xsl:template match="/package/provides">
    <xsl:text>
Provides: </xsl:text>
    <xsl:value-of select="."/>    
  </xsl:template>

  <xsl:template match="/package/requires">
    <xsl:text>
Requires: </xsl:text>
    <xsl:value-of select="."/>    
  </xsl:template>

  <xsl:template match="/package/autoreqprov">
AutoReqProv: <xsl:value-of select="."/>    
  </xsl:template>

  <xsl:template match="prepare">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="build">
OS=linux
BUILD=$RPM_BUILD_DIR
SOURCES=$RPM_SOURCE_DIR
[ -d $BUILD/<xsl:value-of select="$pkgname"/>-<xsl:value-of select="/package/version"/> ] &amp;&amp;\
  cd $BUILD/<xsl:value-of select="$pkgname"/>-<xsl:value-of select="/package/version"/>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="install">
OS=linux
BUILD=$RPM_BUILD_DIR
SOURCES=$RPM_SOURCE_DIR
[ -d $BUILD/<xsl:value-of select="$pkgname"/>-<xsl:value-of select="/package/version"/> ] &amp;&amp; \
  cd $BUILD/<xsl:value-of select="$pkgname"/>-<xsl:value-of select="/package/version"/>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="preinstall">
PREFIX=$RPM_INSTALL_PREFIX
[ "$PREFIX" = "" ] &amp;&amp; PREFIX=/<xsl:value-of select="$prefix"/>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="preuninstall">
PREFIX=$RPM_INSTALL_PREFIX
[ "$PREFIX" = "" ] &amp;&amp; PREFIX=/<xsl:value-of select="$prefix"/>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="postinstall">
PREFIX=$RPM_INSTALL_PREFIX
[ "$PREFIX" = "" ] &amp;&amp; PREFIX=/<xsl:value-of select="$prefix"/>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="postuninstall">
PREFIX=$RPM_INSTALL_PREFIX
[ "$PREFIX" = "" ] &amp;&amp; PREFIX=/<xsl:value-of select="$prefix"/>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="architectures">
   <xsl:for-each select="architecture">
      <xsl:value-of select="."/>
      <xsl:text> </xsl:text>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="archives">
    <xsl:for-each select="archive[@arch=$os or @arch='noarch']">
      <xsl:if test="@extract='yes'">
        <xsl:text>
Source</xsl:text>
        <xsl:value-of select="position()-1"/>
        <xsl:text>: </xsl:text>
        <xsl:apply-templates select="archive-file"/>        
      </xsl:if>
    </xsl:for-each>

  </xsl:template>

  <xsl:template match="archives" mode="extract">

    <xsl:variable name="counter" select="0"/>

    <xsl:for-each select="archive[@arch=$os or @arch='noarch']">
      <xsl:if test="@extract='yes'">
rm -rf $RPM_BUILD_DIR/<xsl:apply-templates select="archive-dir"/>
tar xz -C $RPM_BUILD_DIR -f $RPM_SOURCE_DIR/<xsl:apply-templates select="archive-file"/>

      </xsl:if>
    </xsl:for-each>

  </xsl:template>

  <xsl:template match="files">
    <xsl:apply-templates select="file"/>
  </xsl:template>
  
  <xsl:template match="file">
    <xsl:text>%attr(</xsl:text>

    <xsl:choose>

      <xsl:when test="./type='dir' and @recursive = 'yes' ">
        <xsl:text>-</xsl:text>
      </xsl:when>

      <xsl:otherwise>
        <xsl:value-of select="./mode"/>
      </xsl:otherwise>

    </xsl:choose>

    <xsl:text>,</xsl:text>
    <xsl:apply-templates select="user"/>
    <xsl:text>,</xsl:text>
    <xsl:apply-templates select="group"/>
    <xsl:text>) </xsl:text>
    <xsl:if test="./type='dir' and ( not(@recursive) or @recursive != 'yes' )">
      <xsl:text>%dir </xsl:text>
    </xsl:if>
    <xsl:if test="./config='noreplace'">
      <xsl:text>%config(noreplace) </xsl:text>
    </xsl:if>
    <xsl:apply-templates select="path"/>
    <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="archive-dir">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="archive-file">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="/package/copyright">
    <xsl:text>
License:</xsl:text>
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="/package/license">
    <xsl:text>
License:</xsl:text>
    <xsl:value-of select="."/>
  </xsl:template>

</xsl:stylesheet>
