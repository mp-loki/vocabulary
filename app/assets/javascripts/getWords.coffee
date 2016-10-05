$.get "/words/getWords", (words) ->
    $.each words, (index, word) ->
      logos = $("<div>").addClass("logos").text word.logos
      partOfSpeech = $("<div>").addClass("partOfSpeech").text word.partOfSpeech
      language = $("<div>").addClass("language").text word.language
      $("#words").append $("<li>").append(logos).append(partOfSpeech).append(language)