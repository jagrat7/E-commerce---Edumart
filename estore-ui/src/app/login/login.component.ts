import { Component, OnInit, Input } from '@angular/core';
import { ProductService } from '../product.service';
import { ActivatedRoute, Router } from '@angular/router';
import { SessionService } from '../session.service';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  username: string = '';
  password: string = '';
  message: string = '';
  user_type: string = '';
  public userDetails: any;
  appComponent: typeof AppComponent;

  constructor(
    private productService: ProductService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private sessionService: SessionService
  ) {
    this.appComponent = AppComponent;
  }

  ngOnInit(): void {
    //does nothing on initialization
  }

  login() {
    this.message = '';
    if (this.username.trim() !== '' && this.password.trim() !== '') {
      this.productService
        .authenticateUser(this.username, this.password)
        .subscribe({
          next: (userDetails) => {
            console.log(userDetails);
            this.userDetails = userDetails;
            if (
              userDetails.hasOwnProperty('status') &&
              userDetails.status == 'failure'
            ) {
              this.sessionService.logInGuest();
              this.message = 'Credentials not matched';
            } else {
              if (userDetails.is_admin) {
                this.user_type = 'admin';
              } else if (userDetails.is_author) {
                this.user_type = 'author';
              } else {
                this.user_type = 'customer';
              }
              this.sessionService.logIn(
                userDetails.user_data.username,
                userDetails.user_data.id,
                this.user_type
              );
              this.router.navigate(['home']).then(() => {
                window.location.reload();
              });
            }
          },
        });
    }
  }
  cancel() {
    this.router.navigate(['home']).then(() => {
      window.location.reload();
    });
  }
}
