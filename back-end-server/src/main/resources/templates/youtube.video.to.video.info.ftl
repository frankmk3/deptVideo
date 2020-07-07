<#ftl output_format="JSON" auto_esc=false>
  {
   "id": null,
    "key":"${(video.id)!''}",
    "site":null,
    "size":null,
    "type":null,
    "title":${objectMapper.writeValueAsString((video.snippet.title)!'')},
    "description":${objectMapper.writeValueAsString((video.snippet.description))!''},
    "channelTitle":"${(video.snippet.channelTitle)!''}",
    "duration":"${(video.contentDetails.duration)!''}",
    "definition":"${(video.contentDetails.definition)!''}",
    "tags": ${objectMapper.writeValueAsString(video.snippet.tags)} ,
    "thumbnails":${objectMapper.writeValueAsString(video.snippet.thumbnails)}
}