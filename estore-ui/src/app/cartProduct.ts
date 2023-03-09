export interface cartProduct {
  quantity: number;
  type: String;
  productDetails: {
    id: number;
    name: string;
    description: string;
    price: number;
    topic: String[];
    rating: number;
    review: String[];
    author: String;
    status: Status;
    duration: number;
    resolution: number[];
    format: String[];
    print_type: String;
    pages: number;
    weight: number;
  };
}
export enum Status {
  IN_LIVE = "IN_LIVE",
  TO_BE_ADDED = "TO_BE_ADDED",
  TO_BE_DELETED = "TO_BE_DELETED"
}
