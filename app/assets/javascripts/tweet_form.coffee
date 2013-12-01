$ = jQuery
GPS = geo_position_js

# TODO indicate when geolocation is active
# TODO indicate when image is selected
class TweetForm

  constructor: (@$el) ->
    if GPS.init()
      @$el.on('click', '.js-geolocation', @requestGeolocation)
    else
      @$el.find('.js-geolocation').remove()

  requestGeolocation: =>
    # TODO display success or failure message
    GPS.getCurrentPosition(
      (pos) =>
        @$el.find('[name=geolocation]').val('true')
        @$el.find('[name=longitude]').val(pos.coords.longitude)
        @$el.find('[name=latitude]').val(pos.coords.latitude)
    ,
      (error) =>
        switch error.code
          when 1 # Permission denied
            console.log error.message
          when 2 # Position unavailable
            console.log error.message
          when 3 # Timeout
            console.log error.message
        @$el.find('[name=geolocation]').val('false')
    ,
      enableHighAccuracy: yes
    )

$ -> new TweetForm($('#tweet-form'))

