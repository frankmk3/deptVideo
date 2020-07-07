<#ftl output_format="JSON" auto_esc=false>
<#macro printName valuesList=[]>
    <#local sep ="">
    <#list valuesList as value>
        ${sep}"${(value.name)!''}"
        <#local sep =",">
    </#list>
</#macro>

<#macro printVideos videos=[]>
    <#local sep ="">
    <#list videos as video>
        ${sep}
        <@printVideoInfo video/>
        <#local sep =",">
    </#list>
</#macro>

<#macro printVideoInfo video>
    {
   "id":"${(video.id)!''}",
    "key":"${(video.key)!''}",
    "site":"${(video.site)!''}",
    "size":"${(video.size)!''}",
    "type":"${(video.type)!''}",
    "title":null,
    "description":null,
    "channelTitle":null,
    "duration":null,
    "definition":null,
  "tags":null,
  "thumbnails": null
  }

</#macro>
<#assign separator = "">
{
      "title": "${(info.title)!''}",
      "originalTitle": "${(info.original_title)!''}",
      "overview":  ${objectMapper.writeValueAsString((info.overview)!'')} ,
      "year": null,
      "type": "${(info.type)!''}",
      "originalLanguage": "${(info.original_language)!''}",
      "imdbId": "${(info.imdb_id)!''}",
      "releaseDate": "${(info.release_date)!''}",
      "tagline": "${(info.details.tagline)!''}",
      "posters": [<#if  (info.poster_path)??>"${info.poster_path}"<#assign separator = ","></#if><#if (info.backdrop_path)??>${separator}"${info.backdrop_path}"</#if>],
      "countries": [<@printName info.details.production_countries/>],
      "genres": [<@printName info.details.genres/>],
      "directors": null,
      "actors": null,
      "companies": [<@printName info.details.production_companies/>],
      "spokenLanguages": [<@printName info.details.spoken_languages/>],
      "ratings": [{"source": "themoviedb", "value": ${(info.popularity)!0}}],
      "videos": [<@printVideos (info.details.videos.results)![] />],
      "extraInformation": null
}