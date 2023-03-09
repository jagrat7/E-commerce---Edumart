import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { MessageService } from './message.service';
import { HttpClient, HttpHeaders, HttpRequest } from '@angular/common/http';
import { Product } from './Product';
import { catchError, map, tap } from 'rxjs/operators';
import { SessionService } from './session.service';
import { cartProduct } from './cartProduct';
import { cartSession } from './cartSession';
import { inventoryHistory } from './inventoryHistory';
import { purchaseHistory } from './purchaseHistory';
@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private storeUrl = 'http://localhost:8080/inventory/';
  private productsUrl = 'http://localhost:8080/inventory/products';
  private productSearchUrl = 'http://localhost:8080/inventory/search';
  private userUrl = 'http://localhost:8080/user/';
  private registerUrl = 'http://localhost:8080/user/register';
  private authorUrl = 'http://localhost:8080/user/author';

  private log(message: string) {
    this.messageService.add(`productService: ${message}`);
  }
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      userId:
        this.sessionService.get('user_id') != null
          ? this.sessionService.get('user_id')
          : 'guest',
    }),
  };

  public getHeaders(): any {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      userId:
        this.sessionService.get('user_id') != null
          ? this.sessionService.get('user_id')
          : 'guest',
    });
  }

  public getAuthorHeaders(author_id): any {
      return new HttpHeaders({
         'userId': this.sessionService.get('user_id') != null ? this.sessionService.get('user_id') : "guest",
          'authorId': String(author_id) 
        });  
  }

  public getImageHeaders(): any {
    return new HttpHeaders({
      userId:
        this.sessionService.get('user_id') != null
          ? this.sessionService.get('user_id')
          : 'guest',
    });
  }

  constructor(
    private http: HttpClient,
    private messageService: MessageService,
    private sessionService: SessionService
  ) {}

  public handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead
      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  public handleUserError<T>(result?: T) {
    debugger;
    return (error: any): Observable<T> => {
      return of(result as T);
    };
  }

  getProducts(type): Observable<any> {
    return this.http.get<any>(this.storeUrl + "products", { headers: this.getHeaders() }).pipe(map(response => {

      if ((response.books != null) || (response.books.length === 0)) {
        response.books.forEach(item => { item["type"] = "book" })
      }
      if ((response.videos != null) || (response.videos.length === 0)) {
        response.videos.forEach(item => { item["type"] = "video" })
      }
      console.log(response)
      if (type == "all") { return response.videos.concat(response.books) }
    }))
  }
  getProductsFilter(type): Observable<any> {
    return this.http.get<any>(this.storeUrl + "products"+"?filter=product_requests", { headers: this.getHeaders() }).pipe(map(response => {

      if ((response.books != null) || (response.books.length === 0)) {
        response.books.forEach(item => { item["type"] = "book" })
      }
      if ((response.videos != null) || (response.videos.length === 0)) {
        response.videos.forEach(item => { item["type"] = "video" })
      }
      console.log(response)
      if (type == "all") { return response.videos.concat(response.books) }
    }))
  }


  getProduct(type, id): Observable<any> {
    console.log(this.storeUrl + "product/" + type + "/" + id)
    return this.http.get<any>(this.storeUrl + "product/" + type + "/" + id, { headers: this.getHeaders() }).pipe(map(response => {
      console.log(response)
      if (((response.books === null) || (response.books.length === 0))) {
        response.videos[0]["type"] = "video";
        return response.videos[0]
      } else {
        response.books[0]["type"] = "book"
        return response.books[0]
      }
    }));
  }

  deleteProduct(type, id): Observable<any> {
    console.log(this.storeUrl + "product/" + type + "/" + id)
    return this.http.delete<any>(this.storeUrl + "product/" + type + "/" + id, { headers: this.getHeaders() })
  }

  /* GET heroes whose name contains search term */
  searchProducts(term: string): Observable<any> {
    if (!term.trim()) {
      // if not search term, return empty hero array.
      return of([]);
    }
    return this.http.get<any>(`${this.productSearchUrl}?name=${term}`, { headers: this.getHeaders() }).pipe(map(response => {
      if ((response.books != null)) {
        response.books.forEach(item => { item["type"] = "book" })
      }
      if ((response.videos != null)) {
        response.videos.forEach(item => { item["type"] = "video" })
      }
      console.log(response)
      return response.videos.concat(response.books)
    }))
  }

  authenticateUser(username, password): Observable<any> {
    console.log(this.storeUrl + ", username = " + username + ", password = " + password);
    const body = { username: username, password: password };
    return this.http.post<any>(this.userUrl + "authenticate", body).pipe(map(response => {
      return response;
    }),
      catchError(this.handleUserError<any>({ "status": "failure" }))
    );

  }

  // POST: add a new product to the server
  createProduct(product): Observable<any> {
    console.log(product)
    let response = this.http.post<any>(this.storeUrl + "product", product, { headers: this.getHeaders() }).pipe(
      tap((product) => this.log(`added product id=${product.id}`)),
      catchError(this.handleError<any>('addProduct'))
    );
    console.log(response);
    return response;

  }

  // PUT: update a product in the server
  updateProduct(product): Observable<any> {
    console.log({ type: product.type, product });
    let response = this.http.put<any>(this.storeUrl + "product", product, { headers: this.getHeaders() }).pipe(
      tap(_ => this.log(`updated product id=${product.id}`)),
      catchError(this.handleError<any>('updateProduct'))
    );
    console.log(response);
    return response;

  }
  updateProductRequest(product): Observable<any> {
    console.log({ type: product.type, product });
    let response = this.http.put<any>(this.storeUrl + "product?type=approve_product_request", product, { headers: this.getHeaders() }).pipe(
      tap(_ => this.log(`updated product id=${product.id}`)),
      catchError(this.handleError<any>('updateProduct'))
    );
    console.log(response);
    return response;

  }

  getCartProducts(): Observable<cartProduct[]> {
    console.log(this.storeUrl + 'cart');
    return this.http
      .get<any>(this.storeUrl + 'cart', {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          if (response != null) {
            response.products.forEach((item: any) => { });
          }
          // debugger
          return response.products;
        })
      );
  }
  getCartSessions(): Observable<cartSession[]> {
    console.log(this.storeUrl + 'cart');
    return this.http
      .get<any>(this.storeUrl + 'cart', {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          if (response != null) {

            response.sessions.forEach((item: any) => { });
          }
          console.log(response.sessions)
          return response.sessions;
        })
      );
  }

  deleteCartProduct(cartProduct: cartProduct): Observable<cartProduct[]> {
    console.log(this.storeUrl + 'cart/' + 'delete');
    return this.http
      .delete<any>(this.storeUrl + 'cart', {
        headers: this.getHeaders(),
        body: { type: cartProduct.type, id: cartProduct.productDetails.id },
      })
      .pipe(
        map((response) => {
          return response;
        })
      );
  }
  deleteCartSession(cartSession: cartSession): Observable<cartProduct[]> {
    console.log(this.storeUrl + 'cartsession/' + 'delete');
    return this.http
      .delete<any>(this.storeUrl + 'cartsession', {
        headers: this.getHeaders(),
        body: { name: cartSession.name, time: cartSession.time },
      })
      .pipe(
        map((response) => {
          return response;
        })
      );
  }

  addCartProduct(cartProduct): Observable<any> {
    console.log(this.storeUrl + 'cart/' + 'add');
    return this.http
      .post<any>(this.storeUrl + 'cart', { type: cartProduct.type, id: cartProduct.id }, { headers: this.getHeaders() }
      ).pipe(
        map((response) => {
          return response;
        }),
        catchError(this.handleUserError<any>({ "status": "failure" }))
      );
  }

  modifyCartProduct(cartProduct, isIncrease, quantity): Observable<any> {
    console.log(this.storeUrl + 'cart/' + 'update');
    return this.http
      .put<any>(this.storeUrl + 'cart', { type: cartProduct.type, id: cartProduct.id, isIncrease: isIncrease, quantity: quantity }, { headers: this.getHeaders() }
      ).pipe(
        map((response) => {
          return response;
        }),
        catchError(this.handleUserError<any>({ "status": "failure" }))
      );
  }

  checkout(): Observable<any> {
    console.log(this.storeUrl + 'checkout');
    return this.http
      .post<any>(this.storeUrl + 'checkout', {}, { headers: this.getHeaders() }
      ).pipe(
        map((response) => {
          return response;
        }),
        catchError(this.handleUserError<any>({ "status": "failure" }))
      );
  }

  uploadImage(file: File, id: Number, type: string): Observable<any> {
    const formData: FormData = new FormData();

    formData.append('file', file);
    debugger;

    const req = new HttpRequest(
      'POST',
      this.storeUrl + 'product/images/' + type + '/' + id,
      formData,
      {
        reportProgress: true,
        responseType: 'json',
        headers: this.getImageHeaders(),
      }
    );

    return this.http.request(req);
  }

  deleteImage(fileNames: any, id: Number, type: string): Observable<any> {
    return this.http
      .delete<any>(this.storeUrl + 'product/images/' + type + "/" + id, {
        headers: this.getHeaders(),
        body: { images: fileNames },
      })
      .pipe(
        map((response) => {
          return response;
        }),
        catchError(this.handleUserError<any>({ "status": "failure" }))
      );
  }

  getAuthor(author_id): Observable<any> {
    console.log("get author by id: " + author_id);
    return this.http.get<any>(this.authorUrl, { headers: this.getAuthorHeaders(author_id) }).pipe(
      map((response) => {
        console.log(response);
        return response;
      }),
      catchError(this.handleUserError<any>({ "status": "failure" }))
    );
  }

  addSessionToCart(author_name, time): Observable<any> {
    console.log("add session to cart:" + author_name + time);
    return this.http
      .post<any>(this.storeUrl + 'cartsession', { name: author_name, time: time }, { headers: this.getHeaders() }
      ).pipe(
        map((response) => {
          return response;
        }),
        catchError(this.handleUserError<any>({ "status": "failure" }))
      );
  }
  getPurchaseHistory(): Observable<purchaseHistory[]> {
    console.log(this.storeUrl + 'history/' + 'purchase');
    return this.http
      .get<any>(this.storeUrl + 'history/' + 'purchase', {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return response;
        })
      );
  }
  getInventoryHistory(): Observable<inventoryHistory[]> {
    console.log(this.storeUrl + 'history/' + 'inventory');
    return this.http
      .get<any>(this.storeUrl + 'history/' + 'inventory', {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return response;
        })
      );
  }

  registerUser(username, password): Observable<any> {
    console.log(
      this.userUrl + "register"
    );
    const body = { username: username, password: password };
    return this.http.post<any>(this.userUrl + "register", body).pipe(
      map((response) => {
        return response;
      }),
      catchError(this.handleUserError<any>({ status: 'failure' }))
    );
  }
}
