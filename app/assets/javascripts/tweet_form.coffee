$ = jQuery
GPS = geo_position_js

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

    @$photoInput = @$el.find('[name=photo]')
    @$el.on('change', '[name=photo]', @togglePhotoIndicator)
    @$el.on('click', '[name=photo]', @removePhoto)

  toggleGeolocation: =>
    if @$geolocation.val() is 'true'
      @$geolocation.val('false')
      @_geolocationIndicator off
      return

    GPS.getCurrentPosition(
      (pos) =>
        @$geolocation.val('true')
        @$longitude.val(pos.coords.longitude)
        @$latitude.val(pos.coords.latitude)
        @_geolocationIndicator on
    ,
      (error) =>
        switch error.code
          when 1
            Alerts.pushAlert
              type: 'danger'
              title: 'Permission denied'
              message: error.message
          when 2
            Alerts.pushAlert
              type: 'warning'
              title: 'GPS unavailable'
              message: error.message
          when 3
            Alerts.pushAlert
              type: 'warning'
              title: 'GPS timeout'
              message: error.message
        @$geolocation.val('false')
        @_geolocationIndicator off
    ,
      enableHighAccuracy: yes
    )

  togglePhotoIndicator: =>
    @_photoIndicator on

  removePhoto: (evt) =>
    return unless @$photoInput.val()
    evt.preventDefault()
    @$photoInput.wrap('<form>').closest('form').get(0).reset()
    @$photoInput.unwrap()
    @_photoIndicator off

  _geolocationIndicator: (active) ->
    onClasses = 'btn-success active'
    offClasses = 'btn-default'
    if active
      @$gpsButton.addClass(onClasses).removeClass(offClasses)
    else
      @$gpsButton.addClass(offClasses).removeClass(onClasses)

  _photoIndicator: (active) ->
    onClasses = 'btn-success active'
    offClasses = 'btn-default'
    if active
      @$photoInput.parent('.photo-button').addClass(onClasses).removeClass(offClasses)
    else
      @$photoInput.parent('.photo-button').addClass(offClasses).removeClass(onClasses)

$ -> new TweetForm($('#tweet-form'))

