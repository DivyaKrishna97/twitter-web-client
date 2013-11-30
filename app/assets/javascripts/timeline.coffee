$ = jQuery
T = Handlebars

class Timeline
  template: T.compile($("#template-tweet").html())

  constructor: (@$el, @tweets) ->
    @$el.on('click', '.more-tweets', @loadOlderTweets)

  render: ->
    @$el.html('')
    @_appendingRender(@tweets)

  loadOlderTweets: (evt) =>
    $button = $(evt.target)
    tweetId = $button.data('before-tweet-id')
    $.getJSON('/historical', beforeId: tweetId).then(
      (result) =>
        console.log(result)
        @_appendingRender(result)
        @tweets.push(tweet) for tweet in result
        $button.remove()
    ,
      (errorReason) ->
        # TODO handle this properly
        console.log errorReason
    )
    no # Suppress default event handling

  _appendingRender: (tweets) ->
    [body..., last] = tweets
    @$el.append @template
      tweets: tweets
      beforeId: last.id

$ ->
  timeline = new Timeline($('#timeline'), PageStore.get('timeline'))
  timeline.render()

