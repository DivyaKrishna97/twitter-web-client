$ = jQuery
T = Handlebars

class Timeline
  template: T.compile($("#template-tweet").html())

  constructor: (@$el, @tweets) ->

  render: ->
    @$el.html @template
      tweets: @tweets

$ ->
  timeline = new Timeline($('#timeline'), PageStore.get('timeline'))
  timeline.render()

