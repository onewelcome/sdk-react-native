class ObjectIdHelper {
  id;

  constructor() {
    this.id = 0;
  }

  getNewID(text) {
    this.id++;
    return text + this.id;
  }
}

export default new ObjectIdHelper();
