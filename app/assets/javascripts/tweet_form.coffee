$ = jQuery
GPS = geo_position_js

# TODO indicate when image is selected
class TweetForm

  constructor: (@$el) ->
    if GPS.init()
      @$el.on('click', '.js-geolocation', @toggleGeolocation)
    else
      @$el.find('.js-geolocation').remove()
    @$gpsButton = @$el.find('.js-geolocation')
    @$geolocation = @$el.find('[name=geolocation]')
    @$longitude = @$el.find('[name=longitude]')
    @$latitude = @$el.find('[name=latitude]')

  toggleGeolocation: =>
    if @$geolocation.val() is 'true'
      @$geolocation.val('false')
      @_geolocationIndicator off
      return

    # TODO display success or failure message
    GPS.getCurrentPosition(
      (pos) =>
        @$geolocation.val('true')
        @$longitude.val(pos.coords.longitude)
        @$latitude.val(pos.coords.latitude)
        @_geolocationIndicator on
    ,
      (error) =>
        switch error.code
          when 1 # Permission denied
            console.log error.message
          when 2 # Position unavailable
            console.log error.message
          when 3 # Timeout
            console.log error.message
        @$geolocation.val('false')
        @_geolocationIndicator off
    ,
      enableHighAccuracy: yes
    )

  _geolocationIndicator: (active) ->
    if active
      @$gpsButton.addClass('btn-success active').removeClass('btn-default')
    else
      @$gpsButton.addClass('btn-default').removeClass('btn-success active')

$ -> new TweetForm($('#tweet-form'))

