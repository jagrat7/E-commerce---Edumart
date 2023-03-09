import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormGroup, FormControl, FormArray } from '@angular/forms';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {MatChipInputEvent} from '@angular/material/chips';
import { ProductService } from  '../product.service';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import {MatFormFieldModule} from '@angular/material/form-field';

@Component({
  selector: 'app-admin-add-product-mat',
  templateUrl: './admin-add-product-mat.component.html',
  styleUrls: ['./admin-add-product-mat.component.css']
})
export class AdminAddProductMatComponent implements OnInit {
  [x: string]: any;
  // To handle image upload
  selectedFiles: any[] = [];
  selectedUrl: any[] = [];
  selectedType = "url";
  isBackDone: boolean;
  isSubmitDisable: boolean;
  currentFileData: any;
  // To implement Array upload in a simplest way (not complete but works)
  topic_value: string;
  

  type = [
    {name: 'book'},
    {name: 'video'},
  ]

  constructor(private fb: FormBuilder,
    private storeProductService : ProductService,
    private route: ActivatedRoute, 
    private location: Location) {}

  goBack(): void{
    if(!this.isBackDone){
      this.isBackDone = true;
      this.location.back();
    }
  }

  ngOnInit(): void {
    this.selectedUrl[0] = {url: ""};
    this.isSubmitDisable = true;
    this.addForm = this.fb.group({
      name: [null, Validators.required],
      description: [null],
      price: [0, Validators.compose([Validators.required, Validators.min(0)])],
      topic1 : '',
      topic: [['']],
      rating: [0, Validators.compose([Validators.min(0), Validators.max(5)])],
      review1: '',
      review: [['']],
      author: [''],
      duration: [0],
      format1: '',
      format: [['']],
      print_type1: '',
      print_type: [['']],
      type: ['', Validators.required],
      quantity: [0]
    });
  }

  addUrl(index): void{
    this.selectedUrl[index + 1] = {url : ""};
    this.submitDisableBasedOnImageValue();
  }

  validateImageField(): any{
    if( this.selectedType == "url" && this.selectedUrl.length > 0 && this.selectedUrl[0].url.trim() != "" ){
      return true;
    }else  if( this.selectedType == "local" && this.selectedFiles.length > 0 ){
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
      if(this.selectedFiles.length == 0){
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
  onSubmit(): void {
    let product = this.addForm.value;
    this.isBackDone = false;
    if(!this.validateImageField())
    {
      return;
    }
    let image = this.getImageDetails();
    if(image != undefined){
      product.image = image;
    }
    

    product.topic = [product.topic1]
    product.review = [product.review1]
    product.format = [product.format1]
    product.print_type = [product.print_type1]
    
    console.log(product);

    this.storeProductService.createProduct(product).subscribe(
      {
        next: (createdProduct) => {
          if(image == undefined){
            this.uploadFiles(createdProduct[product.type + "s"][0].id);
          }else{
            this.goBack();
          }
        }
      });
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
    if(event.target.files[0].name.split(".")[1] == "jpg" || event.target.files[0].name.split(".")[1] == "jpeg" || event.target.files[0].name.split(".")[1] == "png"){
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


  uploadFiles(id : Number) {
    console.log(this.selectedFiles);
    if(this.selectedFiles.length != 0){
      this.storeProductService.uploadImage(this.selectedFiles[0], id, this.addForm.value.type).subscribe(
        {  
          next: (response) => {
            if(response.hasOwnProperty("status")){
              if(response.status !== "failure"){
                this.selectedFiles.splice(0, 1);
                if(this.selectedFiles.length == 0){
                  this.goBack();
                }
                this.uploadFiles(id);
              }
            }
          }
         }
      );  
    }
  }





}
