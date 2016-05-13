// Bind a method to an object and cache it
// via: http://webreflection.blogspot.be/2012/11/my-name-is-bound-method-bound.html
// Courtesy: Refine-NER-Extension
Object.defineProperty(Object.prototype, "boundTo", {
  value: function (methodName) {
    var boundName = "__boundTo__" + methodName;
    return this[boundName] || (this[boundName] = this[methodName].bind(this));
  },
});
