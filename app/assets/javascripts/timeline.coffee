$ = jQuery
T = Handlebars

# Long interval because the REST API is rate limited.
POLL_INTERVAL = 60 * 1000 # milliseconds

class Timeline
  statusTemplate: T.compile($('#template-tweet').html())
  messageTemplate: T.compile('Load {{numTweets}} new {{inflection}}')

  constructor: (@$el, @tweets) ->
    @$el.on('click', '.more-tweets', @loadOlderTweets)
    @$el.on('click', '.more-tweets-message', @displayNewerTweets)
    @$statusList = @$el.children('.status-list')
    @$moreTweetsMessage = @$el.find('.more-tweets-message')
    @tweetBuffer = []

  render: ->
    @$statusList.empty()
    @$el.children('.empty-timeline-message').toggle(@tweets.length is 0)
    @_appendingRender(@tweets)

  loadOlderTweets: (evt) =>
    $button = $(evt.target)
    tweetId = $button.data('before-tweet-id')
    $.getJSON('/status/before', id: tweetId).then(
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

  poll: =>
    @pollId = window.setTimeout(=>
      @_fetchNewerTweets().then(@poll, @poll)
    ,
      POLL_INTERVAL
    )

  stopPolling: ->
    window.clearTimeout(@pollId)

  _fetchNewerTweets: =>
    [head, rest...] = @tweets
    $.getJSON('/status/after', id: head?.id or 0).then((result) =>
      @tweetBuffer = result
      @_updateMoreTweetsMessage()
    )

  displayNewerTweets: =>
    @stopPolling()
    tweets = @tweetBuffer
    @tweetBuffer = []
    @_prependingRender(tweets)
    @tweets.unshift.apply(@tweets, tweets)
    @_updateMoreTweetsMessage()
    @poll()

  _updateMoreTweetsMessage: ->
    @$moreTweetsMessage.html(
      @messageTemplate
        numTweets: @tweetBuffer.length
        inflection: if @tweetBuffer.length > 1 then 'Tweets' else 'Tweet'
    )
    @$moreTweetsMessage.toggle(@tweetBuffer.length > 0)

  _appendingRender: (tweets) ->
    return unless tweets.length > 0
    [body..., last] = tweets
    @$statusList.append @statusTemplate
      tweets: tweets
      beforeId: last.id

  _prependingRender: (tweets) ->
    return unless tweets.length > 0
    @$statusList.prepend @statusTemplate
      tweets: tweets
      beforeId: null

$ ->
  timeline = new Timeline($('#timeline'), PageStore.get('timeline'))
  timeline.render()
  timeline.poll()

