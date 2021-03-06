<?xml version="1.0"?>
<!--
Copyright (c) 2016-2018 Zerocracy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to read
the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
merge, publish, distribute, sublicense, and/or sell copies of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">
  <xsl:output method="html" doctype-system="about:legacy-compat" encoding="UTF-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:include href="/xsl/inner-layout.xsl"/>
  <xsl:template match="page" mode="head">
    <title>
      <xsl:text>Gang</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="page" mode="inner">
    <h1>
      <xsl:text>Gang</xsl:text>
    </h1>
    <xsl:apply-templates select="people"/>
  </xsl:template>
  <xsl:template match="people">
    <p>
      <xsl:text>There are </xsl:text>
      <xsl:value-of select="count(user)"/>
      <xsl:text> users already registered with us.</xsl:text>
      <xsl:text> To join us too you have to apply, see </xsl:text>
      <a href="http://datum.zerocracy.com/pages/policy.html#2">
        <xsl:text>&#xA7;2</xsl:text>
      </a>
      <xsl:text>.</xsl:text>
      <xsl:text> If you want these programmers to work with your project,</xsl:text>
      <xsl:text> you have to publish it on the </xsl:text>
      <xsl:text>Board</xsl:text>
      <xsl:text>, as explained in </xsl:text>
      <a href="http://datum.zerocracy.com/pages/policy.html#26">
        <xsl:text>&#xA7;26</xsl:text>
      </a>
      <xsl:text>, we will automatically notify the best</xsl:text>
      <xsl:text> and the most relevant candidates; they will apply, if interested.</xsl:text>
    </p>
    <table data-sortable="true">
      <thead>
        <tr>
          <th>
            <xsl:text>User</xsl:text>
          </th>
          <th>
            <xsl:text>Mentor/</xsl:text>
            <sub>
              <xsl:text>/</xsl:text>
              <a href="http://datum.zerocracy.com/pages/policy.html#1">
                <xsl:text>&#xA7;1</xsl:text>
              </a>
            </sub>
          </th>
          <th>
            <xsl:text>Rate</xsl:text>
            <sub>
              <xsl:text>/</xsl:text>
              <a href="http://datum.zerocracy.com/pages/policy.html#16">
                <xsl:text>&#xA7;16</xsl:text>
              </a>
            </sub>
          </th>
          <th data-sortable-type="numeric">
            <xsl:text>Reputation</xsl:text>
            <sub>
              <xsl:text>/</xsl:text>
              <a href="http://datum.zerocracy.com/pages/policy.html#18">
                <xsl:text>&#xA7;18</xsl:text>
              </a>
            </sub>
          </th>
          <th>
            <xsl:text>Agenda</xsl:text>
          </th>
          <th>
            <xsl:text>Projects</xsl:text>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="user">
          <xsl:sort select="awards" order="descending" data-type="number"/>
        </xsl:apply-templates>
      </tbody>
    </table>
  </xsl:template>
  <xsl:template match="user">
    <tr>
      <td>
        <img src="https://socatar.com/github/{login}/90-90" style="width:30px;height:30px;border-radius:3px;vertical-align:middle;"/>
        <xsl:text> </xsl:text>
        <a href="https://github.com/{login}">
          <xsl:text>@</xsl:text>
          <xsl:value-of select="login"/>
        </a>
        <sub>
          <xsl:text>/</xsl:text>
          <a href="/u/{login}">
            <xsl:text>z</xsl:text>
          </a>
        </sub>
        <xsl:if test="vacation">
          <xsl:text>(on vacation)</xsl:text>
        </xsl:if>
      </td>
      <td>
        <a href="https://github.com/{mentor}">
          <xsl:text>@</xsl:text>
          <xsl:value-of select="mentor"/>
        </a>
        <sub>
          <xsl:text>/</xsl:text>
          <a href="/u/{mentor}">
            <xsl:text>z</xsl:text>
          </a>
        </sub>
      </td>
      <td style="text-align:right;">
        <xsl:choose>
          <xsl:when test="rate">
            <xsl:value-of select="rate"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>&#x2014;</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </td>
      <td>
        <xsl:attribute name="style">
          <xsl:text>text-align:right;color:</xsl:text>
          <xsl:choose>
            <xsl:when test="awards &gt; 256">
              <xsl:text>darkgreen</xsl:text>
            </xsl:when>
            <xsl:when test="awards &lt; 0">
              <xsl:text>darkred</xsl:text>
            </xsl:when>
            <xsl:when test="awards = 0">
              <xsl:text>inherit</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>orange</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <xsl:choose>
          <xsl:when test="awards = 0">
            <xsl:text>&#x2014;</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:if test="awards &gt; 0">
              <xsl:text>+</xsl:text>
            </xsl:if>
            <xsl:value-of select="awards"/>
          </xsl:otherwise>
        </xsl:choose>
      </td>
      <td style="text-align:right;">
        <xsl:value-of select="agenda"/>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="projects"/>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
