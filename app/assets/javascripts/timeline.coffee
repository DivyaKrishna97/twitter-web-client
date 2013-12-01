$ = jQuery
T = Handlebars

class Timeline
  template: T.compile($("#template-tweet").html())

  constructor: (@$el, @tweets) ->
    @$el.on('click', '.more-tweets', @loadOlderTweets)
    @$statusList = @$el.children('.status-list')

  render: ->
    @$statusList.empty()
    @$el.children('.empty-timeline-message').toggle(@tweets.length is 0)
    @_appendingRender(@tweets)

  loadOlderTweets: (evt) =>
    $button = $(evt.target)
    tweetId = $button.data('before-tweet-id')
    $.getJSON('/historical', beforeId: tweetId).then(
      (result) =>
        @_appendingRender(result)
        @tweets.push(tweet) for tweet in result
        $button.remove()
    ,
      (errorReason) ->
        if errorReason.status is 401
          Alerts.pushAlert
            type: 'danger'
            title: 'You are logged out'
            message: 'please refresh the page to log back in'
        if errorReason.status >= 500
          Alerts.pushAlert
            type: 'warning'
            title: 'Server error'
            message: 'please try again later'
    )
    no # Suppress default event handling

  _appendingRender: (tweets) ->
    return unless tweets.length > 0
    [body..., last] = tweets
    @$statusList.append @template
      tweets: tweets
      beforeId: last.id

$ ->
  timeline = new Timeline($('#timeline'), PageStore.get('timeline'))
  timeline.render()

