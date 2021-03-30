class ObjectIdHelper {
  id: number;

  constructor() {
    this.id = 0;
  }

  getNewID(text: string) {
    this.id++;
    return text + this.id;
  }
}

export default new ObjectIdHelper();
