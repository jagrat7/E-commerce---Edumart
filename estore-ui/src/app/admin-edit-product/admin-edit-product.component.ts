import { Component, OnInit } from '@angular/core';
import { ProductService } from  '../product.service';
import { ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, FormArray } from '@angular/forms';
import { Location } from '@angular/common';

@Component({
  selector: 'app-admin-edit-product',
  templateUrl: './admin-edit-product.component.html',
  styleUrls: ['./admin-edit-product.component.css']
})
export class AdminEditProductComponent implements OnInit {
  product;
  form;
  selectedFiles: any[] = [];
  selectedUrl: any[] = [];
  selectedType = "url";
  isBackDone: boolean;
  isSubmitDisable: boolean;
  existingImages: any[] = [];
  toBeDeletedImages: any[] = [];
  currentFileData: any;
  allowedFileTypes: any[] = ["png", "jpg", "jpeg", "PNG"];

  constructor(
    private route: ActivatedRoute,
    private storeService: ProductService,
    private location: Location) { }

  ngOnInit(): void {
    this.selectedUrl[0] = {url: ""};
    this.isSubmitDisable = true;
    this.getProduct();
    this.form = new FormGroup({
      "name": new FormControl(''),
      "description": new FormControl(''),
      "price": new FormControl(0.0),
      "topic": new FormArray([new FormControl(''), new FormControl('')]),
      "rating": new FormControl(0.0),
      "review": new FormControl(['']),
      "author": new FormControl(''),
      "duration": new FormControl(0),
      "format": new FormControl(['']),
      "print_type": new FormControl(['']),
      "type": new FormControl(''),
      
    });
  }

  goBack(): void{
    if(!this.isBackDone){
      this.isBackDone = true;
      this.location.back();
    }
  }

  getProduct(): void{
    const type = String(this.route.snapshot.paramMap.get('type'));
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.storeService.getProduct(type, id).subscribe({next: (products) =>{
      this.product = products;
      this.selectedType = this.product.image.type.toLowerCase();
      if(this.selectedType == "url"){
        this.selectedUrl = [];
        this.product.image.imageSrc.forEach((imageVal) =>{
          let data = {url : imageVal};
          this.selectedUrl.push(data);
        })
      }else if(this.selectedType == "local"){
        this.existingImages = [];
        this.product.image.imageSrc.forEach((imageVal) =>{
          let data = {name : imageVal};
          this.existingImages.push(data);
        })
      }
      this.submitDisableBasedOnImageValue();
    }});
  }
  addUrl(index): void{
    this.selectedUrl[index + 1] = {url : ""};
    this.submitDisableBasedOnImageValue();
  }

  validateImageField(): any{
    if( this.selectedType == "url" && this.selectedUrl.length > 0 && this.selectedUrl[0].url.trim() != "" ){
      return true;
    }else  if( this.selectedType == "local" && ( this.selectedFiles.length > 0 || this.existingImages.length > 0)){
      return true;
    }
    return false;
  }

  submitDisableBasedOnImageValue() : any{
    let isValueChanged = false;
    if(this.selectedType == "url"){
      if(this.selectedUrl.length > 0){
        this.selectedUrl.forEach((value) => {
          if(value.url.trim() == ""){
            this.isSubmitDisable = true;
            isValueChanged = true;
          }
        })
      }else{
        this.isSubmitDisable = true;
        isValueChanged = true;
      }
    }else if(this.selectedType == "local"){
      if(this.selectedFiles.length == 0 && this.existingImages.length == 0){
        this.isSubmitDisable = true;
        isValueChanged = true;
      }
    }
    if(!isValueChanged){
      this.isSubmitDisable = false;
    }
  }

  getImageDetails(){
    if(this.selectedType == "url"){
      let image = {};
      image["type"] = "url";
      let imageSrc = [];
      this.selectedUrl.forEach((value) => {
        if(value.url.trim() != ""){
          imageSrc.push(value.url);
        }
      })
      image["imageSrc"] = imageSrc;
      if(imageSrc != undefined && imageSrc.length != 0){
        return image;
      }
    }
    return undefined;
  }

  onSubmit(product){
    // this.storeProductService.createProduct(product).subscribe(products => this.products = products);
    // this.storeProductService.createProduct(product).subscribe();
    console.log(product)
  }

  save(){
    this.isBackDone = false;
    if(!this.validateImageField())
    {
      return;
    }
    let image = this.getImageDetails();
    let localImages = [];
    if(this.product.image != undefined){
      if(this.product.image.type.toLowerCase() == "local"){
        localImages = this.product.image.imageSrc;
      }
      delete this.product.image;
    }

    if(image != undefined){
      this.product.image = image;
    }
    this.storeService.updateProduct(this.product)
    .subscribe({next: (product) => {
      if(image == undefined){
        this.uploadFiles(product[this.product.type + "s"][0].id);
      }else{
        // this.toBeDeletedImages = localImages;
        // this.deleteExistingImages();
        this.goBack();
      }
    }
  });
  
    console.log(this.product);
  }

  onImageTypeChange(value){
    this.selectedType = value;
    this.submitDisableBasedOnImageValue();
  }

  //Gets called when the user selects an image
  public addFiles(event) {
    //Select File
    if(this.selectedFiles == undefined){
      this.selectedFiles = [];
    }
    if(this.allowedFileTypes.indexOf(event.target.files[0].name.split(".")[1]) > -1){
      this.selectedFiles.push(event.target.files[0]);
    }

    this.currentFileData = "";
    this.submitDisableBasedOnImageValue();
  }

  removeFileAdded(index, file){
    if(this.selectedFiles.length > index && this.selectedFiles.indexOf(file) != -1){
      while (this.selectedFiles.indexOf(file) !== -1) {
        this.selectedFiles.splice(this.selectedFiles.indexOf(file), 1);
      }
    }
    this.submitDisableBasedOnImageValue();
  }

  removeExistingImage(index,file){
    this.existingImages.splice(index,1);
    this.toBeDeletedImages.push(file.name);
    this.submitDisableBasedOnImageValue();
  }


  uploadFiles(id : Number) {
    console.log(this.selectedFiles);
    if(this.selectedFiles.length != 0){
      this.storeService.uploadImage(this.selectedFiles[0], id, this.product.type).subscribe(
        {  
          next: (response) => {
            if(response.hasOwnProperty("status")){
              if(response.status !== "failure"){
                this.selectedFiles.splice(0, 1);
                if(this.selectedFiles.length == 0){
                  this.deleteExistingImages();
                }
                this.uploadFiles(id);
              }
            }
          }
         }
      );  
    }else{
      this.deleteExistingImages();
    }
  }

  deleteExistingImages(){
    if(this.toBeDeletedImages.length != 0){
      this.storeService.deleteImage(this.toBeDeletedImages, this.product.id, this.product.type).subscribe(
        {  
          next: (response) => {
            this.goBack();
          }
         }
      ); 
    }else{
      this.goBack();
    }
  }
}
