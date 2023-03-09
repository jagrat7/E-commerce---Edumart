export interface purchaseHistoryProduct {
  type: String;
  id: number;
  name: String;
  quantity: number;
}

export interface sessionHistoryProduct {
  name: String;
  time: number;
  status: String;
}

export interface purchaseHistory {
  products: purchaseHistoryProduct[];
  sessions: sessionHistoryProduct[];
  timeStamp: number;
}
