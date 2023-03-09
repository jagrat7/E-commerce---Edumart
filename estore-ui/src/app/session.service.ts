import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class SessionService {

  logIn(username : string, user_id : string, user_type : string) {
    localStorage.setItem('user_id', user_id);
    localStorage.setItem('user_type', user_type);
    localStorage.setItem('user_name', username);
  }

  logInGuest() {
    localStorage.setItem('user_id', 'guest');
    localStorage.setItem('user_type', 'guest');
  }

  logOut() {
    localStorage.removeItem("user_name");
    localStorage.removeItem("user_id");
    localStorage.removeItem("user_type");
  }

  get(key : string) : any {
    return localStorage.getItem(key);
  }

  getIsAdmin() : any {
    return localStorage.getItem("user_type") != null && localStorage.getItem("user_type") == "admin";
  }
  getIsAuthor() : any {
    return localStorage.getItem("user_type") != null && localStorage.getItem("user_type") == "author";
  }

  getUserName() : any {
    return localStorage.getItem("user_name");
  }

  getIsLogIn() : any {
    return (localStorage.getItem('user_id') != null && localStorage.getItem('user_id') != 'guest') &&
          (localStorage.getItem('user_type') != null && localStorage.getItem('user_type') != 'guest') &&
          localStorage.getItem('user_name') != null;
  }


}