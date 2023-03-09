export interface field_change {
  field: string;
  old_value: object;
  new_value: object;
}
export interface action {
  type: String;
  sub_type: String;
  product_type: String;
  id: number;
  name: String;
  field_changes: field_change[];
}
export interface inventoryHistory {
  actions: action[];
  timeStamp: number;
}
