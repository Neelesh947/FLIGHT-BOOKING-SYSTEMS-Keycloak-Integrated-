import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerSupportRepresentativeComponent } from './customer-support-representative.component';

describe('CustomerSupportRepresentativeComponent', () => {
  let component: CustomerSupportRepresentativeComponent;
  let fixture: ComponentFixture<CustomerSupportRepresentativeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CustomerSupportRepresentativeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CustomerSupportRepresentativeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
