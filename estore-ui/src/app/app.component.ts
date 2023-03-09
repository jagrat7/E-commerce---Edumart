import { Component } from '@angular/core';

import { Router } from '@angular/router';
import { SessionService } from './session.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  public isAdmin: Boolean;
  public isLogIn: Boolean;
  public userName: string;
  public isAuthor: Boolean;

  constructor(public sessionService : SessionService, private router: Router) { }
  title = 'estore-ui';
  ngOnInit(): void {
    this.isAdmin = this.sessionService.getIsAdmin();
    this.isLogIn = this.sessionService.getIsLogIn();
    this.userName = this.sessionService.getUserName();
    this.isAuthor = this.sessionService.getIsAuthor();
  }

  logOut(){
    this.sessionService.logOut();
    this.router.navigate(['home'])
            .then(() => {
                    window.location.reload();
                 });
  }
  
}

