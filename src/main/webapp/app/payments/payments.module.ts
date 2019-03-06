import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { StripeSharedModule } from 'app/shared';
import { PAYMENTS_ROUTE, PaymentsComponent } from './';

@NgModule({
    imports: [StripeSharedModule, RouterModule.forChild([PAYMENTS_ROUTE]), FormsModule, ReactiveFormsModule],
    declarations: [PaymentsComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class StripePaymentsModule {}
// JHipster Stripe Module will add new line here
