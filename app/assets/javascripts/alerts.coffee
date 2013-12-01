$ = jQuery
T = Handlebars

templateHtml = """
               <div class="alert alert-{{type}}">
                 <button type="button" class="close" data-dismiss="alert" aria-hidden="true">
                   &times;
                 </button>
                 <strong>{{title}}</strong> {{message}}
               </div>
               """

# Display dismissable alert messages near the top of the page.
class Alerts

  template: T.compile(templateHtml)

  constructor: (@$el) ->

  pushAlert: (message) ->
    @$el.append(@template(message))

@Alerts = new Alerts $('.alerts')

