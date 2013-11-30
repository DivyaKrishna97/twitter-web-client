@PageStore = new class
  store: {}
  set: (k, v) -> @store[k] = v
  get: (k) -> @store[k]

